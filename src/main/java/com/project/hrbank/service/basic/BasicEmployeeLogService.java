package com.project.hrbank.service.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.hrbank.dto.response.EmployeeLogResponse;
import com.project.hrbank.entity.EmployeeLogs;
import com.project.hrbank.mapper.EmployeeLogMapper;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.service.EmployeeLogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
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
		String mappedField = FIELD_MAP.getOrDefault(sortField, "changedAt");

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

		// List<EmployeeLogs> employeeLogs = repository.findAll();
		// List<EmployeeLogResponse> responses = employeeLogs.stream()
		// 	.map(employeeLog -> EmployeeLogMapper.INSTANT.toDto(employeeLog))
		// 	.collect(Collectors.toList());
		// return responses;
	}
}
