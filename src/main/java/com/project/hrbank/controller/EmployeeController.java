package com.project.hrbank.controller;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.file.service.FileService;
import com.project.hrbank.service.EmployeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;
	private final FileService fileService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmployeeResponseDto> registerEmployee(
		@RequestPart("employee") @Valid EmployeeRequestDto requestDto,
		@RequestPart(value = "profile", required = false) MultipartFile profileImage
	) {
		EmployeeResponseDto responseDto = employeeService.registerEmployee(requestDto, profileImage);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@GetMapping
	public ResponseEntity<Page<EmployeeResponseDto>> getEmployees(
		@RequestParam(required = false) String nameOrEmail,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "30") int size) {
		Page<EmployeeResponseDto> employees = employeeService.getEmployees(nameOrEmail, page, size);
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
	public ResponseEntity<Long> countEmployees() {
		long count = employeeService.countActiveEmployees();
		return ResponseEntity.ok(count);
	}
}
