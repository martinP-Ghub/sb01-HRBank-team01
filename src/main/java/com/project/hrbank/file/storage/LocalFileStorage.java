package com.project.hrbank.file.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.hrbank.file.dto.FileDto;



@Service
public class LocalFileStorage implements FileStorage {
	private static final String STORAGE_PATH = "files/";

	@Override
	public Long put(Long id, byte[] fileData) {
		try {
			Path filePath = Paths.get(STORAGE_PATH + id);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, fileData);
			return id;
		}catch (IOException e){
			throw new RuntimeException("파일 저장 실패", e);
		}
	}

	@Override
	public InputStream get(Long id) {
		try {
			Path filePath = Paths.get(STORAGE_PATH + id);
			if (!Files.exists(filePath)) {
				throw new FileNotFoundException("파일을 찾을 수 없습니다: " + id);
			}
			return Files.newInputStream(filePath);
		}catch (IOException e){
			throw new RuntimeException("파일 조회 실패", e);
		}
	}

	@Override
	public ResponseEntity<?> download(FileDto fileDto) {
		try {
			InputStream fileStream = get(fileDto.id());
			return ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=\"" + fileDto.fileName() + "\"")
				.body(new InputStreamResource(fileStream));
		}catch (Exception e){
			return ResponseEntity.status(500).body("파일 다운로드 실패");
		}
	}

	@Override
	public boolean delete(Long id) {
		try {
			Path filePath = Paths.get(STORAGE_PATH + id);
			return Files.deleteIfExists(filePath);
		}catch (IOException e){
			throw new RuntimeException("파일 삭제 실패" ,e);
		}
	}
}
