package com.project.hrbank.backup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.backup.dto.BackupDto;
import com.project.hrbank.backup.service.BackupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

	private final BackupService backupService;

	@GetMapping
	public ResponseEntity findAll() {
		return ResponseEntity.ok().build();
	}

	@PostMapping
	public ResponseEntity<BackupDto> backup() {
		BackupDto backup = backupService.backup();
		return ResponseEntity.ok().body(backup);
	}

	@GetMapping("/latest")
	public ResponseEntity findLatest() {
		return ResponseEntity.ok().build();
	}

}
