package com.project.hrbank.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BackupScheduler {

	private final BackupService backupService;

	@Scheduled(cron = "${schedule.backup-cron:data}")
	public void scheduledBackup() {
		backupService.backupBySystem();
	}

}
