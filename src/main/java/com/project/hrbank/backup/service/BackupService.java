package com.project.hrbank.backup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.repository.BackupRepository;
import com.project.hrbank.repository.EmployeeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {

	private final BackupRepository backupRepository;
	private final EmployeeLogRepository employeeLogRepository;

	public CursorPageResponseBackupDto findAll(LocalDateTime cursor, Pageable pageable) {
		cursor = Optional.ofNullable(cursor)
			.orElse(LocalDateTime.now());
		Slice<Backup> slice = backupRepository.findAllBy(cursor, pageable);

		List<BackupDto> content = getBackupContents(slice);

		LocalDateTime nextCursor = null;
		if (!slice.getContent().isEmpty()) {
			nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
		}

		Long nextIdAfter = null;
		if (slice.hasNext()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		long count = backupRepository.count();
		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(), count);
	}

	private List<BackupDto> getBackupContents(Slice<Backup> slice) {
		return slice.getContent()
			.stream()
			.map(this::toDto)
			.toList();
	}

	@Transactional
	public BackupDto backup(String clientIpAddr) {
		// 1. 가장 최근 완료된 배치 작업 시간 조회
		Backup lastEndedAtBackup = getLastEndedAtBackup();
		LocalDateTime endedAt = lastEndedAtBackup.getEndedAt();
		// 2. 가장 최근 작업 시간 이 후에 직원 데이터가 생성되거나 변경이 되었는지 여부 검사
		// 3 - 1 변경 사항이 없다면, '건너뜀'으로 배치 이력을 저장하고 프로세스를 종료한다.
		Backup backup = null;
		if (isNotChangedEmployee(endedAt)) {
			backup = Backup.ofSkipped(clientIpAddr);
			return toDto(backupRepository.save(backup));
		}
		// 3 - 2 데이터 백업 필요 시 백업 이력을 등록한다. 작업자 : IP 주소, 상태 : 진행중 (Backup 엔티티 생성)
		backup = Backup.ofInProgress(clientIpAddr);
		Backup progressBackup = backupRepository.save(backup);

		// 4 백업 작업을 수행한다.
		// 4 - 1 전체 직원 정보를 파일 관리 요구사항에 따라 CSV 파일로 저장한다. -> 여기서 파일을 저장, 저장하는 방법은?
		// 4 - 2 한 번에 처리하는 경우 Out of Memory 이슈가 생길 수 있으니 방법 강구

		// 5 - 2 백업이 실패 => 상태 = 실패, 종료 시간 = 현재 시간, 백업 파일 = 에러 로그 파일 정보
		if (fileBackupReturnResult()) {
			backup.update(LocalDateTime.now(), Status.FAILED);
			// 5 - 2 - 1 생성하던 CSV 파일 삭제한다.
			// 5 - 2 - 2 .log 파일에 에러 로그를 저장한다. {상태} : 실패, {종료 시간} : 현재 시간, {백업 파일} : 에러 로그 파일 정보
			return toDto(backupRepository.save(backup));
		}
		// 5 백업이 성공하면 3-2 에서 생성한 Backup 엔티티의 데이터를 업데이트한다.
		// 5 - 1 백업 성공 => 상태 = 완료, 종료 시간 = 현재시간, 백업 파일 = 백업 파일 정보
		backup.update(LocalDateTime.now(), Status.COMPLETED);
		// file repository 를 이용해서 저장된 파일의 메타데이터를 넘겨서 저장한다.
		return toDto(backup);
	}

	private boolean fileBackupReturnResult() {
		return false;
	}

	private boolean isNotChangedEmployee(LocalDateTime endedAt) {
		return !employeeLogRepository.existsByChangedAtAfter(endedAt);
	}

	public BackupDto findLatest() {
		Backup backup = getLastEndedAtBackup();
		return toDto(backup);
	}

	private Backup getLastEndedAtBackup() {
		return backupRepository.findLastBackup()
			.stream().findFirst()
			.orElseThrow(() -> new NoSuchElementException("Not found Backup"));
	}

	private BackupDto toDto(Backup backup) {
		return BackupDto.toDto(backup);
	}

}
