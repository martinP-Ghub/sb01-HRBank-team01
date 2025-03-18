package com.project.hrbank.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {

	@NotBlank(message = "이름을 입력해주세요.")
	private String name;

	@NotBlank(message = "유효한 이메일 주소를 입력해주세요.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	private Long departmentId;

	@NotBlank(message = "직함을 입력해주세요.")
	private String position;

	@NotNull(message = "입사일을 입력해주세요.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate hireDate;

	private Long profileImageId;
}
