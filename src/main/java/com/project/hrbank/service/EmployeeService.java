package com.project.hrbank.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;

public interface EmployeeService {
	Page<EmployeeResponseDto> getEmployees(String nameOrEmail, String departmentName, String position,
		EmployeeStatus status, int page, int size, String sortField,
		String sortDirection);

	EmployeeResponseDto getEmployeeById(Long id);

	EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto, MultipartFile profileImage);

	EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto, MultipartFile profileImage);

	void deleteEmployee(Long id);

	long countEmployees(EmployeeStatus status, String fromDate, String toDate);

	long countEmployeesHiredInDateRange(LocalDate fromDate, LocalDate toDate);

	List<Map<String, Object>> getEmployeeDistribution(String groupBy, EmployeeStatus status);
	List<Map<String, Object>> getEmployeeStatsTrend(LocalDate from, LocalDate to, String unit);
}
