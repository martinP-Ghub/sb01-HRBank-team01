package com.project.hrbank.file.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.hrbank.file.FileHandlerFactory;
import com.project.hrbank.file.dto.FileDto;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {
	private static final String STORAGE_PATH = "files/";
	private final FileHandlerFactory fileHandlerFactory;
	private final FileRepository fileRepository;

	@Override
	public FileEntity saveFile(Long id, byte[] fileData, String fileName, String contentType) {
		try {
			if (fileName == null || fileName.trim().isEmpty()) {
				fileName = "unknown_file";
			}

			if (fileName.contains("..")) {
				throw new IllegalArgumentException("잘못된 파일명: " + fileName);
			}

			String extension = fileHandlerFactory.getFileExtension(fileName);
			String baseName = fileName.substring(0, fileName.lastIndexOf(".")); // 확장자 제거

			String subDirectory = switch (extension) {
				case "csv" -> "csv";
				case "log" -> "log";
				case "jpg", "jpeg", "png" -> "images";
				default -> "others";
			};
			Path directoryPath = Paths.get(STORAGE_PATH, subDirectory);
			Files.createDirectories(directoryPath);

			String uniqueFileName = generateUniqueFileName(baseName, extension, directoryPath.toString());
			Path filePath = Paths.get(directoryPath.toString(), uniqueFileName);

			Files.write(filePath, fileData);
			long fileSize = Files.size(filePath);

			return new FileEntity(id, uniqueFileName, contentType, fileSize, filePath.toString());
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 실패", e);
		}
	}

	@Override
	public InputStream get(Long id) {
		try {
			FileEntity fileEntity = fileRepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("파일을 찾을 수 없습니다: " + id));

			String filePathStr = fileEntity.getFilePath();
			if (filePathStr == null || filePathStr.isBlank()) {
				throw new FileNotFoundException("파일 경로가 존재하지 않습니다: " + id);
			}

			Path filePath = Paths.get(filePathStr);
			if (!Files.exists(filePath)) {
				throw new FileNotFoundException("파일이 존재하지 않습니다: " + filePathStr);
			}

			return Files.newInputStream(filePath);
		} catch (IOException e) {
			throw new RuntimeException("파일 조회 실패", e);
		}
	}

	@Override
	public InputStream getFileStream(Long id) {
		FileEntity fileEntity = fileRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + id));

		Path filePath = Paths.get(fileEntity.getFilePath());
		if (!Files.exists(filePath)) {
			throw new RuntimeException("파일이 존재하지 않습니다: " + id);
		}

		try {
			return Files.newInputStream(filePath);
		} catch (IOException e) {
			throw new RuntimeException("파일 스트림 생성 실패", e);
		}
	}


	@Override
	public boolean delete(Long id) {
		try {

			FileEntity fileEntity = fileRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));

			String filePathStr = fileEntity.getFilePath();
			if (filePathStr == null || filePathStr.isBlank()) {
				throw new IllegalStateException("파일 경로가 유효하지 않습니다.");
			}

			Path filePath = Paths.get(filePathStr);

			boolean isDeleted = Files.deleteIfExists(filePath);

			fileRepository.delete(fileEntity);

			return isDeleted;
		} catch (IOException e) {
			throw new RuntimeException("파일 삭제 실패: " + e.getMessage(), e);
		}
	}

	private static String generateUniqueFileName(String baseName, String extension, String directoryPath) {
		String newFileName = baseName + "." + extension;
		Path filePath = Paths.get(directoryPath, newFileName);
		int count = 1;

		while (Files.exists(filePath)) {
			newFileName = baseName + "(" + count + ")." + extension;
			filePath = Paths.get(directoryPath, newFileName);
			count++;
		}
		return newFileName;
	}
}
