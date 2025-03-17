package com.project.hrbank.backup.controller;

import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> findAll() {
        CursorPageResponseBackupDto backupDtos = backupService.findAll();
        return ResponseEntity.ok().body(backupDtos);
    }

    @PostMapping
    public ResponseEntity<BackupDto> backup(HttpServletRequest request) {
        String clientIpAddr = request.getRemoteAddr();
        BackupDto backup = backupService.backup();
        return ResponseEntity.ok().body(backup);
    }

    // DASH BOARD 에서 사용
    @GetMapping("/latest")
    public ResponseEntity<BackupDto> findLatest() {
		BackupDto backupDto = backupService.findLatest();
		return ResponseEntity.ok().body(backupDto);
    }

}
