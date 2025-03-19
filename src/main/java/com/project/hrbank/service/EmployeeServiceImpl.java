package com.project.hrbank.service;

import java.io.IOException;

import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;

	@Override
	public EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto) {
		if (employeeRepository.existsByEmail(requestDto.getEmail())) {
			throw new IllegalArgumentException("중복된 이메일입니다.");
		}

		Employee employee = Employee.builder()
			.employeeNumber(generateEmployeeNumber())
			.name(requestDto.getName())
			.email(requestDto.getEmail())
			.departmentId(requestDto.getDepartmentId())
			.position(requestDto.getPosition())
			.hireDate(requestDto.getHireDate())
			.status(EmployeeStatus.ACTIVE)
			.profileImageId(requestDto.getProfileImageId())
			.build();

		employeeRepository.save(employee);
		return convertToDto(employee);
	}

	@Override
	public Page<EmployeeResponseDto> getEmployees(String nameOrEmail, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		if (nameOrEmail != null && !nameOrEmail.isEmpty()) {
			return employeeRepository.findByNameContainingOrEmailContaining(nameOrEmail, nameOrEmail, pageable)
				.map(this::convertToDto);
		} else {
			return employeeRepository.findAll(pageable).map(this::convertToDto);
		}
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

	@Override
	@Transactional
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto, MultipartFile profileImage) {
		Employee existingEmployee = employeeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

		if (dto.getName() != null && !dto.getName().equals(existingEmployee.getName())) {
			existingEmployee.setName(dto.getName());
		}

		if (dto.getEmail() != null && !dto.getEmail().equals(existingEmployee.getEmail())) {
			if (employeeRepository.existsByEmail(dto.getEmail())) {
				throw new IllegalArgumentException("중복된 이메일입니다.");
			}
			existingEmployee.setEmail(dto.getEmail());
		}

		if (dto.getDepartmentId() != null && !dto.getDepartmentId().equals(existingEmployee.getDepartmentId())) {
			existingEmployee.setDepartmentId(dto.getDepartmentId());
		}

		if (dto.getPosition() != null && !dto.getPosition().equals(existingEmployee.getPosition())) {
			existingEmployee.setPosition(dto.getPosition());
		}

		if (dto.getHireDate() != null && !dto.getHireDate().equals(existingEmployee.getHireDate())) {
			existingEmployee.setHireDate(dto.getHireDate());
		}

		if (dto.getStatus() != null && !dto.getStatus().equals(existingEmployee.getStatus())) {
			existingEmployee.setStatus(dto.getStatus());
		}

		if (profileImage != null && !profileImage.isEmpty()) {
			Long profileImageId = saveProfileImage(profileImage); // 파일 저장 메서드 호출
			existingEmployee.setProfileImageId(profileImageId);
		}

		employeeRepository.save(existingEmployee);

		return convertToDto(existingEmployee);
	}

	//파일 저장예시코드
	private Long saveProfileImage(MultipartFile profileImage) {
		try {
			String fileName = profileImage.getOriginalFilename();
			byte[] fileBytes = profileImage.getBytes();

			return 123L;
		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지를 저장하는 데 실패했습니다.", e);
		}
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
