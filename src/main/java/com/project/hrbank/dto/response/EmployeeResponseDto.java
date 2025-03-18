package com.project.hrbank.dto.response;

import lombok.*;

import com.project.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
