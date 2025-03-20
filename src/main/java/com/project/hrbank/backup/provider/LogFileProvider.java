package com.project.hrbank.backup.provider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.hrbank.file.entity.enums.FileExtension;

@Component
public class LogFileProvider {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");

	private final Path DIRECTORY;

	protected LogFileProvider(
		@Value("${hrBank.repository.file-directory:data}") String fileDirectory
	) {
		this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, "logs");

		if (Files.notExists(DIRECTORY)) {
			try {
				Files.createDirectories(DIRECTORY);
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	public void writeErrorLog(Path failedEmployeesLogFilePath, IOException saveException) {
		Path logFilePath = resolveFileName();
		deleteFailEmployeeLogFile(failedEmployeesLogFilePath);

		try (
			BufferedWriter bufferedWriter = Files.newBufferedWriter(logFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
		) {
			bufferedWriter.write("[" + LocalDateTime.now() + "] ERROR: " + saveException.getMessage());
			bufferedWriter.newLine();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private static void deleteFailEmployeeLogFile(Path failedEmployeesLogFilePath) {
		try {
			Files.deleteIfExists(failedEmployeesLogFilePath);
		} catch (IOException exception) {
			// TODO exception message 설정
			throw new RuntimeException("Failed to delete failed log file: " + failedEmployeesLogFilePath.getFileName(), exception);
		}
	}

	private Path resolveFileName() {
		String fileName = DATE_FORMATTER.format(LocalDate.now());
		return DIRECTORY.resolve(fileName).resolve(FileExtension.LOG.getDescription());
	}
}
