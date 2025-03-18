package com.project.hrbank.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.hrbank.entity.EmployeeStatus;

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
public class EmployeeResponseDto {

	private Long id;
	private String name;
	private String email;
	private String employeeNumber;
	private Long departmentId;
	private String departmentName;
	private String position;
	private LocalDate hireDate;
	private EmployeeStatus status;
	private Long profileImageId;
	private LocalDateTime createdAt;

}
