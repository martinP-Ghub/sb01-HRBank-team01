package com.project.hrbank.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/{id}")
	public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
		return departmentService.getDepartmentById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<Page<DepartmentDto>> getAllDepartments(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "30") int size) {

		Page<DepartmentDto> departments = departmentService.getAllDepartments(PageRequest.of(page, size));

		return ResponseEntity.ok(departments);
	}

	@PatchMapping("/{id}")
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
