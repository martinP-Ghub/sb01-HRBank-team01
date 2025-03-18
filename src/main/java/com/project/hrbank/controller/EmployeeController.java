package com.project.hrbank.controller;

import java.time.LocalDate;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.service.EmployeeService;
import com.project.hrbank.service.EmployeeServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
	private final EmployeeService employeeService;
	private final EmployeeServiceImpl employeeServiceImpl;

	@PostMapping
	public ResponseEntity<EmployeeResponseDto> registerEmployee(@RequestBody @Valid EmployeeRequestDto requestDto) {
		EmployeeResponseDto responseDto = employeeService.registerEmployee(requestDto);
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

	@PatchMapping("/{id}")
	public ResponseEntity<EmployeeResponseDto> updateEmployee(
		@PathVariable Long id,
		@RequestBody EmployeeRequestDto requestDto) {
		EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, requestDto);
		return ResponseEntity.ok(updatedEmployee);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countEmployees() {
		long count = employeeServiceImpl.countActiveEmployees();
		return ResponseEntity.ok(count);
	}

}
