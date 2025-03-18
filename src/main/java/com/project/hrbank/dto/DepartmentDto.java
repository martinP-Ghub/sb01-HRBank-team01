package com.project.hrbank.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DepartmentDto(
	Long id,
	String name,
	String description,
	LocalDate establishedDate,
	long employeeCount,
	LocalDateTime createdAt
) {
}
