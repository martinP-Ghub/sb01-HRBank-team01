package com.project.hrbank.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.storage.LocalFileStorage;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocalFileStorageTest {

	private static final String TEST_STORAGE_PATH = "test-files/";

	@InjectMocks
	private LocalFileStorage localFileStorage;

	@Mock
	private FileHandlerFactory fileHandlerFactory;

	@BeforeEach
	void setUp() throws IOException {
		MockitoAnnotations.openMocks(this);
		Files.createDirectories(Paths.get(TEST_STORAGE_PATH, "csv"));
		Files.createDirectories(Paths.get(TEST_STORAGE_PATH, "log"));
		Files.createDirectories(Paths.get(TEST_STORAGE_PATH, "images"));
		Files.createDirectories(Paths.get(TEST_STORAGE_PATH, "others"));
	}

	@AfterEach
	void tearDown() throws IOException {
		// 테스트 후 파일 삭제
		Files.walk(Paths.get(TEST_STORAGE_PATH))
			.map(Path::toFile)
			.forEach(file -> file.delete());
	}

	@Test
	@DisplayName("파일 저장 테스트 - CSV 파일 저장")
	void saveFile_CsvFile_Success() throws IOException {
		byte[] fileData = "test data".getBytes();
		String fileName = "backup.csv";
		String contentType = "text/csv";

		when(fileHandlerFactory.getFileExtension(fileName)).thenReturn("csv");

		FileEntity savedFile = localFileStorage.saveFile(null, fileData, fileName, contentType);

		assertNotNull(savedFile);
		assertEquals("backup.csv", savedFile.getFileName());
		assertEquals("text/csv", savedFile.getContentType());
		assertTrue(Files.exists(Paths.get(savedFile.getFilePath())));
	}

	@Test
	@DisplayName("파일 저장 테스트 - 중복된 파일명이 존재하면 새로운 파일명 생성")
	void saveFile_DuplicateFileName_Success() throws IOException {
		byte[] fileData = "test data".getBytes();
		String fileName = "backup.csv";
		String contentType = "text/csv";

		when(fileHandlerFactory.getFileExtension(fileName)).thenReturn("csv");

		// 첫 번째 파일 저장
		FileEntity firstFile = localFileStorage.saveFile(null, fileData, fileName, contentType);
		assertTrue(Files.exists(Paths.get(firstFile.getFilePath())));

		// 두 번째 동일한 파일 저장 (중복 처리 확인)
		FileEntity secondFile = localFileStorage.saveFile(null, fileData, fileName, contentType);
		assertTrue(Files.exists(Paths.get(secondFile.getFilePath())));

		assertNotEquals(firstFile.getFilePath(), secondFile.getFilePath()); // 서로 다른 파일명이어야 함
		assertTrue(secondFile.getFileName().matches("backup\\(\\d+\\)\\.csv")); // backup(1).csv 형태인지 확인
	}

	@Test
	@DisplayName("파일 저장 실패 테스트 - 잘못된 경로")
	void saveFile_InvalidPath_Failure() {
		byte[] fileData = "test data".getBytes();
		String fileName = "../backup.csv"; // 잘못된 경로
		String contentType = "text/csv";

		when(fileHandlerFactory.getFileExtension(fileName)).thenReturn("csv");

		Exception exception = assertThrows(RuntimeException.class, () ->
			localFileStorage.saveFile(null, fileData, fileName, contentType)
		);

		assertTrue(exception.getMessage().contains("잘못된 파일명: " + fileName));
	}
}