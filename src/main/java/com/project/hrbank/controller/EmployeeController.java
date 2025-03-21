package com.project.hrbank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.enums.EmployeeStatus;
import com.project.hrbank.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmployeeResponseDto> registerEmployee(
		@RequestPart(value = "employee", required = true) @Valid EmployeeRequestDto requestDto,
		@RequestPart(value = "profile", required = false)  MultipartFile profileImage
	) {
		EmployeeResponseDto responseDto = employeeService.registerEmployee(requestDto, profileImage);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

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
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String fromDate,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String toDate
	) {
		if (fromDate != null && toDate != null) {
			LocalDate start = LocalDate.parse(fromDate);
			LocalDate end = LocalDate.parse(toDate);
			long count = employeeService.countEmployeesHiredInDateRange(start, end);
			return ResponseEntity.ok(count);
		} else {
			long count = employeeService.countEmployees(
				status,
				fromDate,
				toDate
			);
			return ResponseEntity.ok(count);
		}
	}

	@GetMapping("/stats/trend")
	public ResponseEntity<Long> countEmployeesByUnit(@RequestParam String unit) {
		long count = employeeService.countEmployeesByUnit(unit);
		return ResponseEntity.ok(count);
	}
	@GetMapping("/stats/distribution")
	public ResponseEntity<List<Map<String, Object>>> getEmployeeDistribution(
		@RequestParam(defaultValue = "department") String groupBy,
		@RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
	) {
		List<Map<String, Object>> distribution = employeeService.getEmployeeDistribution(groupBy, status);
		return ResponseEntity.ok(distribution);
	}


}
