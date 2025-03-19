package com.project.hrbank.backup.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;

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
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.repository.EmployeeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
	private static final Instant POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(4713, 11, 24, 0, 0).toInstant(ZoneOffset.UTC);
	private static final String SYSTEM_NAME = "SYSTEM";

	private final BackupRepository backupRepository;
	private final EmployeeLogRepository employeeLogRepository;
	private final EmployeesLogCsvFileProvider csvProvider;

	public CursorPageResponseBackupDto findAll(
		Instant cursor,
		Status status,
		Instant startedAtFrom,
		Instant startedAtTo,
		Pageable pageable
	) {

		Page<Backup> page = backupRepository.findAllBy(cursor, status, startedAtFrom, startedAtTo, pageable);

		List<BackupDto> content = getBackupContents(page);

		Instant nextCursor = null;
		if (page.hasContent()) {
			nextCursor = content.get(content.size() - 1).startedAt();
		}

		Long nextIdAfter = null;
		if (page.hasNext() && page.hasContent()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}
		// TODO Type 변경 시 수정하기
		// new CursorPageResponse<BackupDto>(content, nextCursor, nextIdAfter, content.size(), page.hasNext(), page.getTotalElements())
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

		Instant lastEndedAtBackupDateTime = getLastEndedAt();
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

	private Instant getLastEndedAt() {
		return backupRepository.findLastBackup().stream()
			.findFirst()
			.map(Backup::getEndedAt)
			.orElse(POSTGRESQL_MIN_TIMESTAMP);
	}

	private boolean isNotChangedEmployeeInfo(Instant endedAt) {
		return !employeeLogRepository.existsByChangedAtAfter(LocalDateTime.ofInstant(endedAt, ZoneOffset.UTC));
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

}
