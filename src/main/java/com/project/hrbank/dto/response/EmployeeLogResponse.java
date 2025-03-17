package com.project.hrbank.dto.response;

import java.time.LocalDateTime;

public record EmployeeLogResponse(
	Long log_id,
	String type,
	String memo,
	String ip,
	LocalDateTime changedAt,
	String changedValue,
	String employeeNumber
) {
}
