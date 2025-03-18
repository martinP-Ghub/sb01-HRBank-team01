package com.project.hrbank.backup.service;

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
import com.project.hrbank.backup.repository.BackupRepository;
import com.project.hrbank.backup.repository.BackupRepositoryImpl;
import com.project.hrbank.repository.EmployeeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
	private static final LocalDateTime POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(4713, 11, 24, 0, 0);
	public static final String SYSTEM_NAME = "SYSTEM";

	private final BackupRepository backupRepository;
	private final EmployeeLogRepository employeeLogRepository;
	private final BackupRepositoryImpl backupRepositoryImpl;

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
		LocalDateTime lastEndedAtBackupDateTime = getLastEndedAt();

		Backup backup = Backup.ofInProgress(clientIpAddr);
		backupRepository.save(backup);

		if (isNotChangedEmployeeInfo(lastEndedAtBackupDateTime)) {
			backup.updateSkipped();
			return toDto(backupRepository.save(backup));
		}

		// 4 백업 작업을 수행한다.
		// 4-1 CVS 파일 만들기 -> 파일을 만드는 책임을 분리하자. UserInfoCsvProvider -> createFile() ->
		// 4-2 생성된 파일에 유저 데이터 적재하기
		// 4-3 생성된 파일을 멀티파트로 변환하기 -> byte[] 를 Multipart 로 변환해주는 책임을 나누자.
		// 4-4 파일 데이터베이스에 저장하기 -> FileService 에 변환된 Multipart 파일을 넘겨준다.
		if (isBackupFail()) {
			backup.updateFailed();
			return toDto(backup);
		}

		backup.updateCompleted(null);
		// file repository 를 이용해서 저장된 파일의 메타데이터를 넘겨서 저장한다.
		return toDto(backup);
	}

	// 임시로 만들어둠. 이후 지워야함
/*	private FileEntity createFileTemp() {
		return new FileEntity("test", "csv", 10L, "test");
	}*/

	private LocalDateTime getLastEndedAt() {
		return backupRepository.findLastBackup().stream()
			.findFirst()
			.map(Backup::getEndedAt)
			.orElse(POSTGRESQL_MIN_TIMESTAMP);
	}

	private boolean isBackupFail() {
		return false;
	}

	private boolean isNotChangedEmployeeInfo(LocalDateTime endedAt) {
		return !employeeLogRepository.existsByChangedAtAfter(endedAt);
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

	public CursorPageResponseBackupDto findWithSearchCondition(LocalDateTime cursor, Status status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		cursor = Optional.ofNullable(cursor)
			.orElse(LocalDateTime.now());
		Slice<Backup> slice = backupRepositoryImpl.findWithSearchCondition(cursor, status, startDate, endDate, pageable);

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
}
