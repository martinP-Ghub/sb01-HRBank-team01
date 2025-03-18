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
	public Page<EmployeeResponseDto> getEmployees(String nameOrEmail, String employeeNumber, String departmentName,
		String position, String hireDateFrom, String hireDateTo,
		EmployeeStatus status, Long lastEmployeeId, int size) {
		Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "employeeId"));
		Page<Employee> employeePage;

		if (lastEmployeeId != null) {
			employeePage = employeeRepository.findByEmployeeIdGreaterThan(lastEmployeeId, pageable);
		} else {
			employeePage = employeeRepository.findAll(pageable);
		}

		return employeePage.map(this::convertToDto);
	}

	public long countActiveEmployees() {
		return employeeRepository.countByStatusIn(Arrays.asList(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE));
	}

	@Override
	public EmployeeResponseDto getEmployeeById(Long id) {
		return employeeRepository.findById(id)
			.map(this::convertToDto)
			.orElse(null);
	}

	@Override
	@Transactional // 트랜잭션 관리 추가
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto) {
		Employee employee = employeeRepository.findById(id).get();

		// 이메일 중복 검사
		if (requestDto.getEmail() != null && !employee.getEmail().equals(requestDto.getEmail())) {
			if (employeeRepository.existsByEmailAndEmployeeIdNot(requestDto.getEmail(), id)) {
				throw new IllegalArgumentException("중복된 이메일입니다.");
			}
			employee.setEmail(requestDto.getEmail());
		}

		// 필드 업데이트 (null 체크 포함)
		if (requestDto.getName() != null) {
			employee.setName(requestDto.getName());
		}
		if (requestDto.getDepartmentId() != null) {
			employee.setDepartmentId(requestDto.getDepartmentId());
		}
		if (requestDto.getPosition() != null) {
			employee.setPosition(requestDto.getPosition());
		}
		if (requestDto.getHireDate() != null) {
			employee.setHireDate(requestDto.getHireDate());
		}

		// 변경된 데이터를 저장 (save 호출은 필요할 수 있음)
		return convertToDto(employee); // 더티 체킹으로 자동 반영됨
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
