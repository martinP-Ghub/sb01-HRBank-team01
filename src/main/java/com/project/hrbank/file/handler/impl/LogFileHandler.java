package com.project.hrbank.file.handler.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.file.converter.FileConverter;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.handler.FileHandler;

public class LogFileHandler implements FileHandler {

	private static final String LOG_STORAGE_PATH = "files/logs";

	@Override
	public boolean supports(String extension) {
		return extension.equalsIgnoreCase("log");	}

	@Override
	public byte[] processFile(MultipartFile file) throws IOException {
		Path filePath = Paths.get(LOG_STORAGE_PATH + file.getOriginalFilename());
		Files.createDirectories(filePath.getParent());

		return FileConverter.convertToUtf8(file, filePath);

	}

	@Override
	public FileEntity handleDownload(Path filePath, String fileName) throws IOException {
		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
			throw new IOException("파일을 읽을 수 없습니다: " + fileName);
		}

		return new FileEntity(null, fileName, "text/plain", Files.size(filePath), filePath.toString());
	}
}
