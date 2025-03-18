package com.project.hrbank.dto.response;

import java.time.LocalDateTime;

public record EmployeeLogResponse(
	Long id,
	String type,
	String memo,
	String ipAddress,
	LocalDateTime at,
	String employeeNumber
) {
}
