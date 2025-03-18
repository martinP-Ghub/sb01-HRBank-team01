package com.project.hrbank.service;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;

import org.springframework.data.domain.Page;

public interface EmployeeService {
	EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto);

	Page<EmployeeResponseDto> getEmployees(String nameOrEmail, String employeeNumber, String departmentName,
		String position, String hireDateFrom, String hireDateTo,
		EmployeeStatus status, Long lastEmployeeId, int size);

	EmployeeResponseDto getEmployeeById(Long id);

	EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto);

	void deleteEmployee(Long id);
}
