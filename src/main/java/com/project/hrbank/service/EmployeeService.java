package com.project.hrbank.service;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
	Page<EmployeeResponseDto> getEmployees(String nameOrEmail, String departmentName, String position,
		EmployeeStatus status, int page, int size, String sortField,
		String sortDirection);

	EmployeeResponseDto getEmployeeById(Long id);

	EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto, MultipartFile profileImage);
	long countActiveEmployees();

	EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto, MultipartFile profileImage);

	void deleteEmployee(Long id);

	long countEmployees(EmployeeStatus status, String fromDate, String toDate);

	long countEmployeesByUnit(String unit); // 시간 단위별 직원 수 조회
}
