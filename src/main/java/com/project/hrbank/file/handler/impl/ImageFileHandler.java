package com.project.hrbank.file.handler.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.handler.FileHandler;

public class ImageFileHandler implements FileHandler {

	private static final String IMAGE_STORAGE_PATH = "files/images";

	@Override
	public boolean supports(String extension) {
		return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png");
	}

	@Override
	public byte[] processFile(MultipartFile file) throws IOException {
		Path filePath = Paths.get(IMAGE_STORAGE_PATH + file.getOriginalFilename());
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, file.getBytes());
		return file.getBytes();
	}

	@Override
	public FileEntity handleDownload(Path filePath, String fileName) throws IOException {
		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
			throw new IOException("파일을 읽을 수 없습니다 : " + fileName);
		}
		return new FileEntity(null, fileName, "image/jpeg", Files.size(filePath), filePath.toString());
	}
}
