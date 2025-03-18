package com.project.hrbank.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Pattern;

public record EmployeeLogRequest(
	@Pattern(regexp = "^(CREATED|UPDATED|DELETED)$")
	String type,
	String memo,
	String ipAddress,
	LocalDateTime at,
	String changedValue,
	String employeeNumber
) {
}
