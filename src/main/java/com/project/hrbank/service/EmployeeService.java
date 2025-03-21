package com.project.hrbank.service;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
	EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto, MultipartFile file);

	Page<EmployeeResponseDto> getEmployees(String nameOrEmail, int page, int size);

	EmployeeResponseDto getEmployeeById(Long id);

	long countActiveEmployees();

	EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto, MultipartFile profileImage);

	void deleteEmployee(Long id);
}
