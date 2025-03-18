package com.project.hrbank.service;

import com.project.hrbank.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {
    DepartmentDto createDepartment(DepartmentDto dto);

    List<DepartmentDto> getAllDepartments();

    DepartmentDto updateDepartment(Long id, DepartmentDto dto);

    void deleteDepartment(Long id);
}
