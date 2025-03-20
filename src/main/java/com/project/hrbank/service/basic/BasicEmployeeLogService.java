package com.project.hrbank.service.basic;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.hrbank.dto.request.EmployeeLogRequest;
import com.project.hrbank.dto.response.CursorPageResponse;
import com.project.hrbank.dto.response.EmployeeLogResponse;
import com.project.hrbank.entity.EmployeeLogs;
import com.project.hrbank.mapper.EmployeeLogMapper;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.service.CursorPaginationService;
import com.project.hrbank.service.EmployeeLogService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicEmployeeLogService implements EmployeeLogService {
	private final EmployeeLogRepository repository;
	private final CursorPaginationService paginationService;

	@Override
	@Transactional
	public CursorPageResponse<EmployeeLogResponse> getLogs(LocalDateTime cursor, String employeeNumber, String memo,
		String ipAddress, String type, LocalDateTime atFrom, LocalDateTime atTo, Pageable pageable) {
		return paginationService.getPaginatedResults(
			cursor,
			pageable,
			repository,
			EmployeeLogMapper.INSTANT::toDto,
			EmployeeLogs::getChangedAt,
			EmployeeLogs::getLog_id,
			(cur, page) -> repository.findAll(cur, employeeNumber, memo, ipAddress, type, atFrom, atTo, page)
		);
	}

	@Override
	public String getLogById(Long id) {
		EmployeeLogs response = repository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("저장되지 않았거나, 삭제된 아이디입니다." + id));

		String diff = response.getChangedValue();

		return diff;
	}

	@Override
	public long getLogCount() {
		return repository.count();
	}

	@Override
	public EmployeeLogResponse createLog(EmployeeLogRequest request) {
		EmployeeLogs log = EmployeeLogMapper.INSTANT.toEntity(request);

		return EmployeeLogMapper.INSTANT.toDto(repository.save(log));
	}
}
