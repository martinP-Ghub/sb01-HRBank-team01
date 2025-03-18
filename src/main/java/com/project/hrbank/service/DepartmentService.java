package com.project.hrbank.service;

import java.util.List;

import com.project.hrbank.dto.DepartmentDto;

public interface DepartmentService {
	DepartmentDto createDepartment(DepartmentDto dto);

	List<DepartmentDto> getAllDepartments();

	DepartmentDto updateDepartment(Long id, DepartmentDto dto);

	void deleteDepartment(Long id);
}
