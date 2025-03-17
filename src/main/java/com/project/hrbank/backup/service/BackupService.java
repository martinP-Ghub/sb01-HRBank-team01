package com.project.hrbank.backup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.repository.BackupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {

	private final BackupRepository backupRepository;

	public CursorPageResponseBackupDto findAll(LocalDateTime cursor, Pageable pageable) {
		Slice<Backup> slice = backupRepository.findAllBy(Optional.ofNullable(cursor).orElse(LocalDateTime.now()), pageable);

		List<BackupDto> content = slice.getContent()
			.stream()
			.map(this::toDto)
			.toList();

		boolean hasNext = slice.hasNext();

		LocalDateTime nextCursor = null;
		if (!slice.getContent().isEmpty()) {
			nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
		}

		Long nextIdAfter = hasNext ? content.get(content.size() - 1).id() : null;
		long count = backupRepository.count();
		return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), hasNext, count);
	}

	@Transactional
	public BackupDto backup() {
		// 1. 가장 최근 완료된 배치 작업 시간 조회
		// 2. 가장 최근 작업 시간 이 후에 직원 데이터가 생성되거나 변경이 되었는지 여부 검사

		// 3 - 1 변경 사항이 없다면, '건너뜀'으로 배치 이력을 저장하고 프로세스를 종료한다.
		// 3 - 2 데이터 백업 필요 시 백업 이력을 등록한다. 작업자 : IP 주소, 상태 : 진행중 (Backup 엔티티 생성)

		// 4 백업 작업을 수행한다.
		// 4 - 1 전체 직원 정보를 파일 관리 요구사항에 따라 CSV 파일로 저장한다. -> 여기서 파일을 저장, 저장하는 방법은?
		// 4 - 2 한 번에 처리하는 경우 Out of Memory 이슈가 생길 수 있으니 방법 강구

		// 5 백업이 성공하면 3-2 에서 생성한 Backup 엔티티의 데이터를 업데이트한다.
		// 5 - 1 백업 성공 => 상태 = 완료, 종료 시간 = 현재시간, 백업 파일 = 백업 파일 정보
		// 5 - 2 백업이 실패 => 상태 = 실패, 종료 시간 = 현재 시간, 백업 파일 = 에러 로그 파일 정보
		// 5 - 2 - 1 생성하던 CSV 파일 삭제한다.
		// 5 - 2 - 2 .log 파일에 에러 로그를 저장한다. {상태} : 실패, {종료 시간} : 현재 시간, {백업 파일} : 에러 로그 파일 정보
		return null;
	}

	public BackupDto findLatest() {
		Pageable pageable = PageRequest.of(0, 1);
		return backupRepository.findLastBackup(pageable)
			.stream().findFirst().map(this::toDto)
			.orElseThrow(() -> new NoSuchElementException(""));
	}

	private BackupDto toDto(Backup backup) {
		return BackupDto.toDto(backup);
	}

}

/**
 * **배치에 의한 데이터 백업**
 * <p>
 * - 데이터 백업 프로세스를 일정한 주기(1시간)마다 자동으로 반복합니다. - 배치 주기는 애플리케이션 설정을 통해 주입할 수 있어야 합니다. - `Spring Scheduler`를 활용해 구현하세요. -
 * **{작업자}**는 `system`으로 입력합니다.
 */
