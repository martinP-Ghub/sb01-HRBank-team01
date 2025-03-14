package com.project.hrbank.backup.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BackupScheduler {

	private final BackupService backupService;

	// application 설정으로 주입 받기
	@Scheduled(cron = "${schedule.backup-cron:data}")
	public void scheduledBackup() {
		backupService.backup();
	}

}
