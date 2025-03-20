package com.project.hrbank.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.entity.EmployeeStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
}

