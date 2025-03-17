package com.project.hrbank.service;

import java.util.Map;

public interface EmployeeLogService {
	Map<String, Object> getLogs(String sortField, String sortDirection, int size);
	// List<EmployeeLogResponse> getLogs(String sortField, String sortDirection, int size);

}
