package com.project.hrbank.dto.request;

import java.time.LocalDate;

import com.project.hrbank.entity.enums.EmployeeStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {
	private String name;
	private String email;
	private Long departmentId;
	private String position;
	private LocalDate hireDate;
	private EmployeeStatus status;
	private Long profileImageId;
	private String memo;
}

