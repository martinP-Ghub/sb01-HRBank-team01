package com.project.hrbank.controller;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.service.EmployeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@GetMapping
	public ResponseEntity<Page<EmployeeResponseDto>> getEmployees(
		@RequestParam(required = false) String nameOrEmail,
		@RequestParam(required = false) String departmentName,
		@RequestParam(required = false) String position,
		@RequestParam(required = false) EmployeeStatus status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "30") int size,
		@RequestParam(defaultValue = "name") String sortField,
		@RequestParam(defaultValue = "asc") String sortDirection
	) {
		Page<EmployeeResponseDto> employees = employeeService.getEmployees(nameOrEmail, departmentName, position,
			status, page, size, sortField, sortDirection);
		return ResponseEntity.ok(employees);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
		EmployeeResponseDto employee = employeeService.getEmployeeById(id);
		return ResponseEntity.ok(employee);
	}

	@PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmployeeResponseDto> updateEmployee(
		@PathVariable Long id,
		@RequestPart(value = "employee", required = true) EmployeeRequestDto employeeDetails,
		@RequestPart(value = "profile", required = false) MultipartFile profileImage
	) {
		EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, employeeDetails, profileImage);
		return ResponseEntity.ok(updatedEmployee);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countEmployees(
		@RequestParam(required = false) EmployeeStatus status,
		@RequestParam(required = false) String fromDate,
		@RequestParam(required = false) String toDate
	) {
		long count = employeeService.countEmployees(status, fromDate, toDate);
		return ResponseEntity.ok(count);
	}

	@GetMapping("/stats/trend")
	public ResponseEntity<Long> countEmployeesByUnit(@RequestParam String unit) {
		long count = employeeService.countEmployeesByUnit(unit);
		return ResponseEntity.ok(count);
	}
}
