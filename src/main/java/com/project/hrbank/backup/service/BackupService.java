package com.project.hrbank.backup.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.provider.CsvProvider;
import com.project.hrbank.backup.repository.BackupRepository;
import com.project.hrbank.backup.repository.BackupRepositoryImpl;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.repository.FileRepository;
import com.project.hrbank.file.storage.FileStorage;
import com.project.hrbank.repository.EmployeeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
	private static final LocalDateTime POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(4713, 11, 24, 0, 0);
	public static final String SYSTEM_NAME = "SYSTEM";
	public static final String CSV_CONTENT_TYPE = ".csv";

	private final BackupRepository backupRepository;
	private final EmployeeLogRepository employeeLogRepository;
	private final FileRepository fileRepository;
	private final FileStorage fileStorage;

	private final BackupRepositoryImpl backupRepositoryImpl;
	private final CsvProvider csvProvider;

	public CursorPageResponseBackupDto findAll(LocalDateTime cursor, Pageable pageable) {
		cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());
		Slice<Backup> slice = backupRepository.findAllBy(cursor, pageable);

		List<BackupDto> content = getBackupContents(slice);

		LocalDateTime nextCursor = null;
		if (!slice.getContent().isEmpty()) {
			nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
		}

		Long nextIdAfter = null;
		if (slice.hasNext()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		long count = backupRepository.count();
		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(), count);
	}

	private List<BackupDto> getBackupContents(Slice<Backup> slice) {
		return slice.getContent()
			.stream()
			.map(this::toDto)
			.toList();
	}

	public void backupBySystem() {
		backup(SYSTEM_NAME);
	}

	@Transactional
	public BackupDto backup(String clientIpAddr) {

		Backup backup = generateBackup(clientIpAddr);

		LocalDateTime lastEndedAtBackupDateTime = getLastEndedAt();
		if (isNotChangedEmployeeInfo(lastEndedAtBackupDateTime)) {
			backup.updateSkipped();
			return toDto(backupRepository.save(backup));
		}

		// 백업 파일 생성
		// 1 fileId = backupId(다음으로 생성될 ID 디비에서 가져오기), fileName, createAt (파일을 생성 필요 데이터)

		// 2 생성된 파일에 Employees 정보를 적재

		// 3. 성공

		// 4. 실패

		// 마지막 엔티티 저장 fileId, fileName, fileContent, fileSize, filePath (엔티티 저장 필요 데이터)
		// -> FileEntity 생성 한 후 저장했던 Backup 엔티티에 업데이트 해주면 될듯

		try {
			FileEntity generateBackupFile = generateBackupFile();
			backup.updateCompleted(generateBackupFile);
		} catch (RuntimeException exception) {
			backup.updateFailed();
			// 로그 파일에 데이터 쓰기
		}

		return toDto(backup);
	}

	private Backup generateBackup(String clientIpAddr) {
		Backup backup = Backup.ofInProgress(clientIpAddr);
		backupRepository.save(backup);
		return backup;
	}

	private LocalDateTime getLastEndedAt() {
		return backupRepository.findLastBackup().stream()
			.findFirst()
			.map(Backup::getEndedAt)
			.orElse(POSTGRESQL_MIN_TIMESTAMP);
	}

	private boolean isNotChangedEmployeeInfo(LocalDateTime endedAt) {
		return !employeeLogRepository.existsByChangedAtAfter(endedAt);
	}

	private FileEntity generateBackupFile() {
		String fileName = csvProvider.generateFileName();
		byte[] employeeData = csvProvider.loadEmployeeData();
		Path path = Paths.get("system.user", "files");
		Long length = (long)employeeData.length;
		FileEntity fileEntity = fileRepository.save(new FileEntity(null, fileName, "csv", length, path.toString()));
		return fileStorage.saveFile(fileEntity.getId(), employeeData, fileName, CSV_CONTENT_TYPE);
	}

	public BackupDto findLatest() {
		Backup backup = getLastBackup();
		return toDto(backup);
	}

	private Backup getLastBackup() {
		return backupRepository.findLastBackup()
			.stream()
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("Not found Backup"));
	}

	private BackupDto toDto(Backup backup) {
		return BackupDto.toDto(backup);
	}

	public CursorPageResponseBackupDto findWithSearchCondition(
		LocalDateTime cursor, Status status,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	) {
		cursor = Optional.ofNullable(cursor)
			.orElse(LocalDateTime.now());
		Slice<Backup> slice = backupRepositoryImpl.findWithSearchCondition(cursor, status, startDate, endDate,
			pageable);

		List<BackupDto> content = getBackupContents(slice);

		LocalDateTime nextCursor = null;
		if (!slice.getContent().isEmpty()) {
			nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
		}

		Long nextIdAfter = null;
		if (slice.hasNext()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		long count = backupRepository.count();
		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(),
			count);
	}
}
