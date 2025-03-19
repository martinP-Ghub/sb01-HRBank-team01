package com.project.hrbank.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.hrbank.dto.DepartmentDto;

public interface DepartmentService {
	DepartmentDto createDepartment(DepartmentDto dto);

	DepartmentDto getDepartmentById(Long id);

	Page<DepartmentDto> getAllDepartments(Pageable pageable, String search);

	DepartmentDto updateDepartment(Long id, DepartmentDto dto);

	void deleteDepartment(Long id);
}
