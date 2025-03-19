package com.project.hrbank.backup.dto.response;

import java.time.Instant;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;

public record BackupDto(Long id, String worker, Instant startedAt, Instant endedAt, Status status, Long fileId) {

	public static BackupDto toDto(Backup backup) {
		return new BackupDto(backup.getId(), backup.getWorker(), backup.getStartedAt(), backup.getEndedAt(), backup.getStatus(), backup.getFileId());
	}
}
