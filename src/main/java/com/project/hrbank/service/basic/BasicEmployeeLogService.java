package com.project.hrbank.service.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.hrbank.dto.requset.EmployeeLogRequest;
import com.project.hrbank.dto.response.EmployeeLogResponse;
import com.project.hrbank.entity.EmployeeLogs;
import com.project.hrbank.mapper.EmployeeLogMapper;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.service.EmployeeLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicEmployeeLogService implements EmployeeLogService {
	private final EmployeeLogRepository repository;

	private static final Map<String, String> FIELD_MAP = Map.of(
		"at", "changedAt",
		"type", "type",
		"ip", "ip"
	);

	@Override
	// public List<EmployeeLogResponse> getLogs(String sortField, String sortDirection, int size) {
	public Map<String, Object> getLogs(String sortField, String sortDirection, int size) {
		// 요청 필드를 DB 필드로 변환
		String mappedField = FIELD_MAP.getOrDefault(sortField, "changed_at");

		// 정렬 방향 설정
		Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, mappedField);

		// 정렬된 데이터 조회
		List<EmployeeLogs> employeeLogs = repository.findAll(sort).stream().limit(size).toList();

		// DTO 변환
		List<EmployeeLogResponse> responses = employeeLogs.stream()
			.map(EmployeeLogMapper.INSTANT::toDto)
			.collect(Collectors.toList());

		// content에 데이터 넣기
		Map<String, Object> result = new HashMap<>();
		result.put("content", responses);

		return result;
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
