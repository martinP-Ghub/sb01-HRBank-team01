package com.project.hrbank.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.service.EmployeeService;
import com.project.hrbank.service.EmployeeServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	private final EmployeeService employeeService;
	private final EmployeeServiceImpl employeeServiceImpl;

	@Autowired
	public EmployeeController(EmployeeService employeeService, EmployeeServiceImpl employeeServiceImpl) {
		this.employeeService = employeeService;
		this.employeeServiceImpl = employeeServiceImpl;
	}

	@PostMapping
	public ResponseEntity<EmployeeResponseDto> registerEmployee(@RequestBody @Valid EmployeeRequestDto requestDto) {
		EmployeeResponseDto responseDto = employeeService.registerEmployee(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@GetMapping
	public ResponseEntity<Page<EmployeeResponseDto>> getEmployees(
		@RequestParam(required = false) String nameOrEmail,
		@RequestParam(required = false) String employeeNumber,
		@RequestParam(required = false) String departmentName,
		@RequestParam(required = false) String position,
		@RequestParam(required = false) String hireDateFrom,
		@RequestParam(required = false) String hireDateTo,
		@RequestParam(required = false) EmployeeStatus status,
		@RequestParam(required = false) Long lastEmployeeId,
		@RequestParam(defaultValue = "30") int size) {
		Page<EmployeeResponseDto> employees = employeeService.getEmployees(
			nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo, status, lastEmployeeId,
			size);
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
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) Long departmentId,
		@RequestParam(required = false) String position,
		@RequestParam(required = false) String hireDate
	) {
		EmployeeRequestDto requestDto = new EmployeeRequestDto();

		if (name != null)
			requestDto.setName(name);
		if (email != null)
			requestDto.setEmail(email);
		if (departmentId != null)
			requestDto.setDepartmentId(departmentId);
		if (position != null)
			requestDto.setPosition(position);
		if (hireDate != null)
			requestDto.setHireDate(LocalDate.parse(hireDate));

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

