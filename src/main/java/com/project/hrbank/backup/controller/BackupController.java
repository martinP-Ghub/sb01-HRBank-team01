package com.project.hrbank.backup.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.service.BackupService;
import com.project.hrbank.config.paging.DefaultSortField;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

	private final BackupService backupService;

	@GetMapping

	public ResponseEntity<CursorPageResponseBackupDto> findAll(
		@RequestParam(required = false) LocalDateTime cursor,
		@RequestParam(required = false) Status status,
		@RequestParam(required = false, name = "startedAtFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtFrom,
		@RequestParam(required = false, name = "startedAtTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtTo,
		@DefaultSortField("startedAt")
		Pageable pageable
	) {

		CursorPageResponseBackupDto backupDto = backupService.findAll(cursor, status, startedAtFrom, startedAtTo, pageable);
		return ResponseEntity.ok().body(backupDto);
	}

	@PostMapping
	public ResponseEntity<BackupDto> backup(HttpServletRequest request) {
		String clientIpAddr = request.getRemoteAddr();
		BackupDto backup = backupService.backup(clientIpAddr);
		return ResponseEntity.ok().body(backup);
	}

	@GetMapping("/latest")
	public ResponseEntity<BackupDto> findLatest() {
		BackupDto backupDto = backupService.findLatest();
		return ResponseEntity.ok().body(backupDto);
	}

}
