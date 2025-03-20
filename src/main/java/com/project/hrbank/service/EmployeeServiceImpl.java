package com.project.hrbank.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hrbank.dto.request.EmployeeRequestDto;
import com.project.hrbank.dto.response.EmployeeResponseDto;
import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.EmployeeLogs;
import com.project.hrbank.entity.EmployeeStatus;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private final JdbcTemplate jdbcTemplate;

	private final EmployeeRepository employeeRepository;
	private final EmployeeLogRepository employeeLogRepository;
	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Override
	public EmployeeResponseDto registerEmployee(EmployeeRequestDto requestDto) {
		if (employeeRepository.existsByEmail(requestDto.getEmail())) {
			throw new IllegalArgumentException("중복된 이메일입니다.");
		}
		String employeeNumber = generateEmployeeNumber();

		Employee employee = Employee.builder()
			.employeeNumber(employeeNumber)
			.name(requestDto.getName())
			.email(requestDto.getEmail())
			.departmentId(requestDto.getDepartmentId())
			.position(requestDto.getPosition())
			.hireDate(requestDto.getHireDate())
			.status(EmployeeStatus.ACTIVE)
			.build();
		Employee savedEmployee = employeeRepository.save(employee);


		List<Map<String, Object>> logData = new ArrayList<>();
		logData.add(createLogEntry("employee_number", null, savedEmployee.getEmployeeNumber()));
		logData.add(createLogEntry("hire_date", null, employee.getHireDate().toString()));
		logData.add(createLogEntry("name", null, employee.getName()));
		logData.add(createLogEntry("position", null, employee.getPosition()));
		logData.add(createLogEntry("department", null, String.valueOf(employee.getDepartmentId())));
		logData.add(createLogEntry("email", null, employee.getEmail()));
		logData.add(createLogEntry("status", null, employee.getStatus().toString()));

		saveLog("CREATED", logData, savedEmployee.getEmployeeNumber());

		return convertToDto(employee);
	}

	@Override
	public Page<EmployeeResponseDto> getEmployees(String nameOrEmail, String departmentName, String position,
		EmployeeStatus status, int page, int size, String sortField, String sortDirection) {
		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
		Pageable pageable = PageRequest.of(page, size, sort);

		return employeeRepository.findFilteredEmployees(
			departmentName,
			position,
			status,
			pageable
		).map(this::convertToDto);
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

	@Override//@Transactional 사용위치 확인 후 수정 클래스? 메서드? // 코드 컨벤션 지켜서 작성하기
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto, MultipartFile profileImage) {
		Employee existingEmployee = employeeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

		List<Map<String, Object>> logData = new ArrayList<>();

		if (!Objects.equals(existingEmployee.getHireDate(), dto.getHireDate())) {
			logData.add(createLogEntry("hire_date",
				existingEmployee.getHireDate() != null ? existingEmployee.getHireDate().toString() : null,
				dto.getHireDate() != null ? dto.getHireDate().toString() : null));
			existingEmployee.setHireDate(dto.getHireDate());
		}

		if (!Objects.equals(existingEmployee.getName(), dto.getName())) {
			logData.add(createLogEntry("name", existingEmployee.getName(), dto.getName()));
			existingEmployee.setName(dto.getName());
		}

		if (!Objects.equals(existingEmployee.getPosition(), dto.getPosition())) {
			logData.add(createLogEntry("position", existingEmployee.getPosition(), dto.getPosition()));
			existingEmployee.setPosition(dto.getPosition());
		}

		if (!Objects.equals(existingEmployee.getDepartmentId(), dto.getDepartmentId())) {
			logData.add(createLogEntry("department",
				existingEmployee.getDepartmentId() != null ? String.valueOf(existingEmployee.getDepartmentId()) : null,
				dto.getDepartmentId() != null ? String.valueOf(dto.getDepartmentId()) : null));
			existingEmployee.setDepartmentId(dto.getDepartmentId());
		}

		if (!Objects.equals(existingEmployee.getEmail(), dto.getEmail())) {
			logData.add(createLogEntry("email", existingEmployee.getEmail(), dto.getEmail()));
			existingEmployee.setEmail(dto.getEmail());
		}

		if (!Objects.equals(existingEmployee.getStatus(), dto.getStatus())) {
			logData.add(createLogEntry("status",
				existingEmployee.getStatus() != null ? existingEmployee.getStatus().toString() : null,
				dto.getStatus() != null ? dto.getStatus().toString() : null));
			existingEmployee.setStatus(dto.getStatus());
		}

		// 프로필 이미지 처리
		if (profileImage != null && !profileImage.isEmpty()) {
			Long profileImageId = saveProfileImage(profileImage);
			logData.add(createLogEntry("profile_image",
				existingEmployee.getProfileImageId() != null ? String.valueOf(existingEmployee.getProfileImageId()) :
					null,
				profileImageId != null ? String.valueOf(profileImageId) : null));
			existingEmployee.setProfileImageId(profileImageId);
		}

		String employeeNumber = existingEmployee.getEmployeeNumber();

		saveLog("UPDATED", logData, employeeNumber);

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
		Employee employee = employeeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

		List<Map<String, Object>> logData = new ArrayList<>();

		logData.add(createLogEntry("employee_number", employee.getEmployeeNumber(), null));
		logData.add(createLogEntry("hire_date",
			employee.getHireDate() != null ? employee.getHireDate().toString() : null,
			null));
		logData.add(createLogEntry("name", employee.getName(), null));
		logData.add(createLogEntry("position", employee.getPosition(), null));
		logData.add(createLogEntry("department",
			employee.getDepartmentId() != null ? String.valueOf(employee.getDepartmentId()) : null,
			null));
		logData.add(createLogEntry("email", employee.getEmail(), null));
		logData.add(createLogEntry("status",
			employee.getStatus() != null ? employee.getStatus().toString() : null,
			null));

		saveLog("DELETED", logData, employee.getEmployeeNumber());

		employeeRepository.deleteById(id);
	}

	private String generateEmployeeNumber() {
		long count = employeeRepository.count() + 1;
		return String.format("EMP%06d", count);

	}

	@Override
	public long countEmployees(EmployeeStatus status, String fromDate, String toDate) {
		LocalDate start = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : null;
		LocalDate end = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : null;

		return employeeRepository.countEmployees(status, start, end);
	}

	@Override
	public long countEmployeesByUnit(String unit) {
		switch (unit.toLowerCase()) {
			case "day":
				return employeeRepository.countEmployeesForToday();
			case "week":
				return employeeRepository.countEmployeesForCurrentWeek();
			case "month":
				return employeeRepository.countEmployeesForCurrentMonth();
			case "quarter":
				return employeeRepository.countEmployeesForCurrentQuarter();
			case "year":
				return employeeRepository.countEmployeesForCurrentYear();
			default:
				throw new IllegalArgumentException("Invalid unit: " + unit);
		} //enum으로 변경 가능한지
	}

	private void saveLog(String type, List<Map<String, Object>> logEntries, String employeeNumber) {
		Map<String, Object> finalLog = new HashMap<>();
		finalLog.put("type", type);
		finalLog.put("changes", logEntries);

		try {
			String jsonString = new ObjectMapper().writeValueAsString(finalLog);
			logger.info("로그 출력: {}", jsonString);

			// 모든 NOT NULL 컬럼에 더미 값 추가
			String sql = """
            INSERT INTO employee_change_logs 
                (type, changed_value, ip, employee_number, changed_at)
            VALUES 
                (?, ?::jsonb, ?, ?, ?)
            """;

			jdbcTemplate.update(
				sql,
				type,               // type (필수)
				jsonString,         // changed_value (JSONB로 캐스팅)
				"127.0.0.1",        // 더미 IP
				employeeNumber,        // 더미 직원 번호
				LocalDateTime.now() // changed_at (현재 시간)
			);

		} catch (JsonProcessingException e) {
			logger.error("JSON 변환 실패", e);
		}
	}



	private Map<String, Object> createLogEntry(String propertyName, String before, String after) {
		Map<String, Object> entry = new HashMap<>();
		entry.put("propertyName", propertyName);
		entry.put("before", before);
		entry.put("after", after);
		return entry;
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
