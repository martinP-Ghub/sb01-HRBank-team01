package com.project.hrbank.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.config.paging.DefaultSortField;
import com.project.hrbank.dto.response.BackupResponse;
import com.project.hrbank.dto.response.CursorPageResponse;
import com.project.hrbank.entity.enums.Status;
import com.project.hrbank.service.BackupService;
import com.project.hrbank.util.IpUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

	private final BackupService backupService;
	private final IpUtils ipUtils;

	@GetMapping
	@DefaultSortField("startedAt")
	public ResponseEntity<CursorPageResponse<BackupResponse>> findAll(
		@RequestParam(required = false) LocalDateTime cursor,
		@RequestParam(required = false) Status status,
		@RequestParam(required = false, name = "startedAtFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtFrom,
		@RequestParam(required = false, name = "startedAtTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtTo,
		@RequestParam(required = false) String worker,
		Pageable pageable
	) {

		CursorPageResponse<BackupResponse> backupDto = backupService.findAll(cursor, status, startedAtFrom, startedAtTo, worker, pageable);
		return ResponseEntity.ok(backupDto);
	}

	@PostMapping
	public ResponseEntity<BackupResponse> backup(HttpServletRequest request) {
		String clientIpAddr = ipUtils.extractClientIp(request);
		BackupResponse backup = backupService.backup(clientIpAddr);
		return ResponseEntity.ok(backup);
	}

	@GetMapping("/latest")
	public ResponseEntity<BackupResponse> findLatest() {
		BackupResponse backupDto = backupService.findLatest();
		return ResponseEntity.ok(backupDto);
	}

}
