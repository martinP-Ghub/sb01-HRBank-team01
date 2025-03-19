package com.project.hrbank.backup.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.repository.EmployeeRepository;

@Component
public class EmployeesLogCsvFileProvider {

	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	public static final String BACKUP_FILE_NAME = "backup_employee";
	public static final String CSV_HEADER_CONTENT = "ID,직원번호,이름,부서,직급,입사일,상태";
	private static final String CSV_EXTENSION = "csv";

	private final EmployeeRepository employeeRepository;
	private final Path DIRECTORY;

	public EmployeesLogCsvFileProvider(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
		DIRECTORY = Paths.get(System.getProperty("user.dir"), "/files", "employee_log");

		if (Files.notExists(DIRECTORY)) {
			try {
				Files.createDirectory(DIRECTORY);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public FileEntity saveEmployeeLogFile(Long backupId) {
		String fileName = generateFileName(backupId);
		Path filePathName = resolve(fileName);

		try (
			BufferedWriter bufferedWriter = Files.newBufferedWriter(filePathName, StandardCharsets.UTF_8)
		) {
			bufferedWriter.write(CSV_HEADER_CONTENT);

			int page = 0;
			int batchSize = 1000;
			Page<Employee> employeePage;

			do {
				Pageable pageable = PageRequest.of(page, batchSize, Sort.by("employeeId").ascending());
				employeePage = employeeRepository.findAll(pageable);

				for (Employee employee : employeePage.getContent()) {
					bufferedWriter.write(writeEmployeeInfo(employee));
				}

				page++;
			} while (employeePage.hasNext());

			long fileSize = Files.size(filePathName);
			return new FileEntity(fileName, "csv", fileSize, filePathName.getParent().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String generateFileName(Long backupId) {
		String formattedDateTime = LocalDateTime.now().format(TIME_FORMAT);
		return String.format("%s_%d_%s.%s", BACKUP_FILE_NAME, backupId, formattedDateTime, CSV_EXTENSION);
	}

	private Path resolve(String fileName) {
		return DIRECTORY.resolve(fileName);
	}

	private String writeEmployeeInfo(Employee employee) {
		return String.format("%d, %s, %s, %s, %s, %s, %s\n",
			employee.getEmployeeId(),
			employee.getEmployeeNumber(),
			employee.getName(),
			employee.getDepartmentId(),
			employee.getPosition(),
			employee.getHireDate(),
			employee.getStatus()
		);
	}
}
