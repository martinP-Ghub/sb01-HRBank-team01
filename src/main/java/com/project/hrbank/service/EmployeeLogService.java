package com.project.hrbank.service;

import java.util.Map;

import com.project.hrbank.dto.requset.EmployeeLogRequest;
import com.project.hrbank.dto.response.EmployeeLogResponse;

public interface EmployeeLogService {
	Map<String, Object> getLogs(String sortField, String sortDirection, int size);
	// List<EmployeeLogResponse> getLogs(String sortField, String sortDirection, int size);

	String getLogById(Long id);

	Integer getLogCount();

	EmployeeLogResponse createLog(EmployeeLogRequest request);

}
