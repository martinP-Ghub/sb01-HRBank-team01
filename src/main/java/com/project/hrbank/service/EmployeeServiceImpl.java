package com.project.hrbank.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.repository.EmployeeRepository;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto) {
		if (employeeRepository.existsByEmail(requestDto.getEmail())) {
			throw new IllegalArgumentException("중복된 이메일입니다.");
		}

		Employee employee = new Employee();
		employee.setName(requestDto.getName());
		employee.setEmail(requestDto.getEmail());
		employee.setDepartmentId(requestDto.getDepartmentId());
		employee.setPosition(requestDto.getPosition());
		employee.setHireDate(requestDto.getHireDate());
		employee.setStatus(EmployeeStatus.ACTIVE);
		employee.setEmployeeNumber(generateEmployeeNumber());

		if (requestDto.getProfileImageId() != null) {
			employee.setProfileImageId(requestDto.getProfileImageId());
		}

		Employee savedEmployee = employeeRepository.save(employee);
		return convertToDto(savedEmployee);
	}

	@Override
	public Page<EmployeeResponseDto> getEmployees(String nameOrEmail, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Employee> employeePage;

		if (nameOrEmail != null && !nameOrEmail.isEmpty()) {
			employeePage = employeeRepository.findByNameContainingOrEmailContaining(nameOrEmail, nameOrEmail, pageable);
		} else {
			employeePage = employeeRepository.findAll(pageable);
		}

		return employeePage.map(this::convertToDto);
	}

	@Override
	public EmployeeResponseDto getEmployeeById(Long id) {
		return employeeRepository.findById(id)
			.map(this::convertToDto)
			.orElse(null);
	}
	@Override
	public long countActiveEmployees() {
		return employeeRepository.countByStatus(EmployeeStatus.ACTIVE);
	}

	@Transactional
	@Override
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto) {
		// 기존 직원 정보 가져오기
		Employee employee = employeeRepository.findById(id).get();

		// 클라이언트가 제공한 값으로 업데이트
		employee.setName(requestDto.getName());
		employee.setEmail(requestDto.getEmail());
		employee.setDepartmentId(requestDto.getDepartmentId());
		employee.setPosition(requestDto.getPosition());
		employee.setHireDate(requestDto.getHireDate());
		employee.setStatus(requestDto.getStatus());

		// 변경된 데이터를 저장
		Employee updatedEmployee = employeeRepository.save(employee);

		// DTO로 변환하여 반환
		return convertToDto(updatedEmployee);
	}

	@Override
	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}

	private String generateEmployeeNumber() {
		long count = employeeRepository.count() + 1;
		return String.format("EMP%06d", count);
	}

	private EmployeeResponseDto convertToDto(Employee employee) {
		return EmployeeResponseDto.builder()
			.id(employee.getEmployeeId())
			.name(employee.getName())
			.email(employee.getEmail())
			.employeeNumber(employee.getEmployeeNumber())
			.departmentId(employee.getDepartmentId())
			.position(employee.getPosition())
			.hireDate(employee.getHireDate())
			.status(employee.getStatus())
			.profileImageId(employee.getProfileImageId())
			.createdAt(employee.getCreatedAt())
			.build();
	}
}
