package com.project.hrbank.service.basic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.project.hrbank.dto.request.EmployeeLogRequest;
import com.project.hrbank.dto.response.CursorPageResponse;
import com.project.hrbank.dto.response.EmployeeLogResponse;
import com.project.hrbank.entity.EmployeeLogs;
import com.project.hrbank.mapper.EmployeeLogMapper;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.service.EmployeeLogService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicEmployeeLogService implements EmployeeLogService {
	private final EmployeeLogRepository repository;

	@Override
	@Transactional
	public CursorPageResponse<EmployeeLogResponse> getLogs(LocalDateTime cursor, Pageable pageable) {
		cursor = Optional.ofNullable(cursor)
			.orElse(LocalDateTime.now());

		Slice<EmployeeLogs> slice = repository.findAllBy(cursor, pageable);

		List<EmployeeLogResponse> content = slice.getContent()
			.stream()
			.map(EmployeeLogMapper.INSTANT::toDto)
			.toList();

		LocalDateTime nextCursor = null;
		if (!slice.getContent().isEmpty()) {
			nextCursor = slice.getContent().get(slice.getContent().size() - 1).getChangedAt();
		}

		Long nextIdAfter = null;
		if (slice.hasNext()) {
			nextIdAfter = content.get(content.size() - 1).id();
		}

		long count = repository.count();

		return new CursorPageResponse<EmployeeLogResponse>(content, nextCursor, nextIdAfter, content.size(),
			slice.hasNext(), count);
	}

	@Override
	public String getLogById(Long id) {

		try {
			EmployeeLogs response = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("저장되지 않았거나, 삭제된 아이디입니다." + id));

			String diff = response.getChangedValue();

			return diff;
		} catch (NullPointerException e) {
			throw new NullPointerException("ID를 찾을 수 없습니다." + e.getMessage());
		}
	}

	@Override
	public Integer getLogCount() {
		return repository.findAll().size();
	}

	@Override
	public EmployeeLogResponse createLog(EmployeeLogRequest request) {
		EmployeeLogs log = EmployeeLogMapper.INSTANT.toEntity(request);

		return EmployeeLogMapper.INSTANT.toDto(repository.save(log));
	}
}
