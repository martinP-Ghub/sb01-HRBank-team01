package com.project.hrbank.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.project.hrbank.dto.DepartmentDto;
import com.project.hrbank.dto.response.CursorPageResponse;

public interface DepartmentService {
	DepartmentDto createDepartment(DepartmentDto dto);

	DepartmentDto getDepartmentById(Long id);

	CursorPageResponse<DepartmentDto> getAllDepartments(LocalDateTime cursor, String nameOrDescription,
		Pageable pageable);

	DepartmentDto updateDepartment(Long id, DepartmentDto dto);

	void deleteDepartment(Long id);
}
