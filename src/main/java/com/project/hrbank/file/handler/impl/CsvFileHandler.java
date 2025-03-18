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

import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.handler.FileHandler;

public class CsvFileHandler implements FileHandler {
	private static final String CSV_STORAGE_PATH = "files/csv/";


	@Override
	public boolean supports(String extension) {
		return extension.equalsIgnoreCase("csv");
	}

	@Override
	public void processFile(MultipartFile file) throws IOException {
		Path filePath = Paths.get(CSV_STORAGE_PATH + file.getOriginalFilename());
		Files.createDirectories(filePath.getParent());

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	@Override
	public FileEntity handleDownload(Path filePath, String fileName) throws IOException {
		if(!Files.exists(filePath) || !Files.isReadable(filePath)) {
			throw new IOException("파일을 읽을 수 없습니다 : " + fileName);
		}

		return new FileEntity(null, fileName, "text/csv", Files.size(filePath),filePath.toString());
	}
}
