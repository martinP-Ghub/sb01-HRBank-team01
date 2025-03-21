package com.project.hrbank.util.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.enums.FileExtension;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.repository.EmployeeRepository;

@Component
public class EmployeesLogCsvFileProvider {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	private static final String BACKUP_FILE_NAME = "backup_employee";
	private static final String CSV_HEADER_CONTENT = "ID,직원번호,이름,부서,직급,입사일,상태";

	private final Path DIRECTORY;
	private final EmployeeRepository employeeRepository;
	private final LogFileProvider logFileProvider;

	protected EmployeesLogCsvFileProvider(
		EmployeeRepository employeeRepository,
		LogFileProvider logFileProvider,
		@Value("${hrBank.repository.file-directory:data}") String fileDirectory
	) {
		this.employeeRepository = employeeRepository;
		this.logFileProvider = logFileProvider;
		// TODO 하드 코딩 된 부분 제거
		DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, "csv");

		if (Files.notExists(DIRECTORY)) {
			try {
				Files.createDirectories(DIRECTORY);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Optional<FileEntity> saveEmployeeLogFile(Long backupId) {
		String fileName = generateFileName(backupId);
		Path employeesLogFilePath = resolveFilename(fileName);

		try (
			BufferedWriter bufferedWriter = Files.newBufferedWriter(employeesLogFilePath, StandardCharsets.UTF_8)
		) {
			bufferedWriter.write(CSV_HEADER_CONTENT);
			bufferedWriter.newLine();

			Page<Employee> employeePage;
			do {
				int page = 0;
				int batchSize = 1000;
				Pageable pageable = PageRequest.of(page, batchSize, Sort.by("employeeId").ascending());
				employeePage = employeeRepository.findAll(pageable);

				for (Employee employee : employeePage.getContent()) {
					bufferedWriter.write(writeEmployeeInfo(employee));
					bufferedWriter.newLine();
				}

				page++;
			} while (employeePage.hasNext());

			long fileSize = Files.size(employeesLogFilePath);
			FileEntity fileEntity = new FileEntity(fileName, FileExtension.CSV.getDescription(), fileSize, employeesLogFilePath.toString());

			return Optional.of(fileEntity);
		} catch (IOException saveException) {
			logFileProvider.writeErrorLog(employeesLogFilePath, saveException);
			return Optional.empty();
		}
	}

	private String generateFileName(Long backupId) {
		String formattedDateTime = LocalDateTime.now().format(TIME_FORMAT);
		return String.format("%s_%d_%s.%s", BACKUP_FILE_NAME, backupId, formattedDateTime, FileExtension.CSV.getDescription());
	}

	private Path resolveFilename(String fileName) {
		return DIRECTORY.resolve(fileName);
	}

	private String writeEmployeeInfo(Employee employee) {
		return String.format("%d, %s, %s, %s, %s, %s, %s",
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
