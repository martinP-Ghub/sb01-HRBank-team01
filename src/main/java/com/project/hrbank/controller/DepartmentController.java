package com.project.hrbank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.dto.DepartmentDto;
import com.project.hrbank.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

	private final DepartmentService departmentService;

	@PostMapping
	public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto dto) {
		DepartmentDto createdDepartment = departmentService.createDepartment(dto);
		return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
		List<DepartmentDto> departments = departmentService.getAllDepartments();
		return ResponseEntity.ok(departments);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DepartmentDto> updateDepartment(
		@PathVariable Long id,
		@RequestBody DepartmentDto dto) {
		DepartmentDto updatedDepartment = departmentService.updateDepartment(id, dto);
		return ResponseEntity.ok(updatedDepartment);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);
		return ResponseEntity.noContent().build();
	}
}
