package com.project.hrbank.backup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.provider.EmployeesLogCsvFileProvider;
import com.project.hrbank.backup.repository.BackupRepository;
import com.project.hrbank.backup.repository.BackupRepositoryImpl;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.repository.EmployeeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
	private static final LocalDateTime POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(4713, 11, 24, 0, 0);
	private static final String SYSTEM_NAME = "SYSTEM";

	private final BackupRepository backupRepository;
	private final EmployeeLogRepository employeeLogRepository;
	private final BackupRepositoryImpl backupRepositoryImpl;
	private final EmployeesLogCsvFileProvider csvProvider;

	public CursorPageResponseBackupDto findAll(
		LocalDateTime cursor,
		Status status,
		Pageable pageable
	) {
		cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());

		Page<Backup> page = backupRepository.findAllBy(cursor, status, pageable);

		List<BackupDto> content = getBackupContents(page);

		LocalDateTime nextCursor = null;
		if (page.hasContent()) {
			nextCursor = content.get(content.size() - 1).startedAt();
		}

		Long nextIdAfter = null;
		if (page.hasNext() && page.hasContent()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), page.hasNext(), page.getTotalElements());
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

		generateBackupFile(backup);

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

	private void generateBackupFile(Backup backup) {
		try {
			FileEntity fileEntity = csvProvider.saveEmployeeLogFile(backup.getId());
			backup.updateCompleted(fileEntity);
		} catch (RuntimeException exception) {
			backup.updateFailed();
		}
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
		LocalDateTime cursor,
		Status status,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	) {
		cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());
		Page<Backup> page = backupRepositoryImpl.findWithSearchCondition(cursor, status, startDate, endDate, pageable);

		List<BackupDto> content = getBackupContents(page);

		LocalDateTime nextCursor = null;
		if (page.hasContent()) {
			nextCursor = content.get(content.size() - 1).startedAt();
		}

		// 다음 페이지 존재 여부 확인
		Long nextIdAfter = null;
		if (page.hasNext() && page.hasContent()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), page.hasNext(), page.getTotalElements());
	}
}
