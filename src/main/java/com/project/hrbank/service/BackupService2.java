package com.project.hrbank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.dto.response.BackupResponse;
import com.project.hrbank.entity.Backup;
import com.project.hrbank.repository.BackupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService2 {

	/**
	 * 기존의 파일 백업 방식과 다르게 구현 - Async
	 */
	private final BackupRepository backupRepository;

	@Transactional
	public BackupResponse performBackup(String reqAddr) {
		Backup backup = Backup.ofInProgress(reqAddr);
		// Async 방식으로 메소드 호출
		// fileService.createFile(backup.getId()) -> 내부에서 성공 실패 여부에 따라서 상태 변경해주기?
		Backup save = backupRepository.save(backup);
		return BackupResponse.toDto(save);
	}
}
