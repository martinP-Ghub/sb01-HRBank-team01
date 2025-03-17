package com.project.hrbank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.service.EmployeeLogService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/change-logs")
@AllArgsConstructor
public class EmployeeLogRestController {

	private final EmployeeLogService service;

	@GetMapping
	// public List<EmployeeLogResponse> getLogList(
	public Map<String, Object> getLogList(
		@RequestParam(defaultValue = "changedAt") String sortField,
		@RequestParam(defaultValue = "desc") String sortDirection,
		@RequestParam(defaultValue = "30") int size
	) {
		Map<String, Object> result = new HashMap<>();

		return service.getLogs(sortField, sortDirection, size);
	}

}
