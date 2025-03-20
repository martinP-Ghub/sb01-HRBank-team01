package com.project.hrbank.backup.controller;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

	private final BackupService backupService;

	@GetMapping
	public ResponseEntity<CursorPageResponseBackupDto> findAll(
		@RequestParam(required = false) Instant cursor,
		@RequestParam(required = false) Status status,
		@RequestParam(required = false, name = "startedAtFrom") Instant startedAtFrom,
		@RequestParam(required = false, name = "startedAtTo") Instant startedAtTo,
		@PageableDefault(
			size = 30,
			page = 0,
			sort = "startedAt",
			direction = Sort.Direction.DESC
		) Pageable pageable
	) {
		cursor = Optional.ofNullable(cursor).orElse(Instant.now());
		startedAtFrom = Optional.ofNullable(startedAtFrom).orElse(Instant.EPOCH);
		startedAtTo = Optional.ofNullable(startedAtTo).orElse(Instant.now());
		CursorPageResponseBackupDto backupDto = backupService.findAll(cursor, status, startedAtFrom, startedAtTo, pageable);
		return ResponseEntity.ok().body(backupDto);
	}

	@PostMapping
	public ResponseEntity<BackupDto> backup(HttpServletRequest request) {
		String clientIpAddr = request.getRemoteAddr();
		BackupDto backup = backupService.backup(clientIpAddr);
		return ResponseEntity.ok().body(backup);
	}

	// DASH BOARD 에서 사용
	@GetMapping("/latest")
	public ResponseEntity<BackupDto> findLatest() {
		BackupDto backupDto = backupService.findLatest();
		return ResponseEntity.ok().body(backupDto);
	}

}
