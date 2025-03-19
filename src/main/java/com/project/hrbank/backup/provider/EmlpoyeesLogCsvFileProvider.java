package com.project.hrbank.backup.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.repository.EmployeeRepository;

@Component
public class EmlpoyeesLogCsvFileProvider {

	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	public static final String BACKUP_FILE_NAME = "backup_employee";
	public static final String CSV_HEADER_CONTENT = "ID,직원번호,이름,부서,직급,입사일,상태";
	private static final String CSV_EXTENDSION = ".csv";

	private final EmployeeRepository employeeRepository;
	private final Path DIRECTORY;

	public EmlpoyeesLogCsvFileProvider(EmployeeRepository employeeRepository) {
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

	public byte[] loadEmployeeData() {
		PrintWriter printWriter = new PrintWriter(new StringWriter());
		printWriter.println(CSV_HEADER_CONTENT);
		List<Employee> employees = employeeRepository.findAll();
		employees.forEach(employee -> writeEmployeeInfo(employee, printWriter));
		return printWriter.toString().getBytes(StandardCharsets.UTF_8);
	}

	public FileEntity saveEmployeeLogFile(Long backupId) {
		String fileName = generateFileName(backupId);
		Path filePathName = DIRECTORY.resolve(fileName);

		try (
			BufferedWriter bufferedWriter = Files.newBufferedWriter(filePathName)
		) {
			bufferedWriter.write(CSV_HEADER_CONTENT);

			long fileSize = Files.size(filePathName);
			return new FileEntity(fileName, "csv", fileSize, filePathName.getParent().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String generateFileName(Long backupId) {
		String formattedDateTime = LocalDateTime.now().format(TIME_FORMAT);
		return String.format("%s_%d_%s%s", BACKUP_FILE_NAME, backupId, formattedDateTime, CSV_EXTENDSION);
	}

	private Path resolve(String fileName) {
		return DIRECTORY.resolve(fileName);
	}

	private void writeEmployeeInfo(Employee employee, PrintWriter printWriter) {
		printWriter.printf("%d, %s, %s, %s, %s, %s, %s\n",
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
