package com.project.hrbank.service;

import org.springframework.data.domain.Page;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;

public interface EmployeeService {
	EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto);

	Page<EmployeeResponseDto> getEmployees(String nameOrEmail, int page, int size);

	EmployeeResponseDto getEmployeeById(Long id);

	long countActiveEmployees();

	EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto);

	void deleteEmployee(Long id);
}
