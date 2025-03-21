package com.project.hrbank.dto.response;

import java.time.LocalDateTime;

import com.project.hrbank.entity.Backup;
import com.project.hrbank.entity.enums.Status;

public record BackupResponse(Long id, String worker, LocalDateTime startedAt, LocalDateTime endedAt, Status status, Long fileId) {

	public static BackupResponse toDto(Backup backup) {
		return new BackupResponse(backup.getId(), backup.getWorker(), backup.getStartedAt(), backup.getEndedAt(), backup.getStatus(), backup.getFileId());
	}
}
