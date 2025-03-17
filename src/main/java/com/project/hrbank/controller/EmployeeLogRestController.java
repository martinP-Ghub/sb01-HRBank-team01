package com.project.hrbank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.service.EmployeeLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public class EmployeeLogRestController {

	private final EmployeeLogService service;

	/**
	 *
	 * @param sortField - 정렬 기준
	 * @param sortDirection - 정렬 ASC, DESC
	 * @param size - 페이지 당 로딩 개수
	 * @return Log List 반환
	 */
	@GetMapping
	@Operation(summary = "직원 정보 수정 이력 목록 조회", description = "직원 정보 수정 이력 목록을 조회합니다. 상세 변경 내용은 포함되지 않습니다.")
	// public List<EmployeeLogResponse> getLogList(
	public Map<String, Object> getLogs(
		@RequestParam(defaultValue = "at") String sortField,
		@RequestParam(defaultValue = "desc") String sortDirection,
		@RequestParam(defaultValue = "30") int size
	) {
		Map<String, Object> result = new HashMap<>();

		return service.getLogs(sortField, sortDirection, size);
	}

	/**
	 *
	 * @param id - 상세 변경사항 검색 id
	 * @return 변경 상세 내역 반환
	 */
	@GetMapping("{id}/diffs")
	public String getLogById(@PathVariable Long id) {
		return service.getLogById(id);
	}

	/**
	 *
	 * @return 변경된 목록 개수 반환
	 */
	@GetMapping("/count")
	public Integer getLogCount() {
		return service.getLogCount();
	}
}
