package com.project.hrbank.file;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.hrbank.file.handler.FileHandler;

@Component
public class FileHandlerFactory {
	private final List<FileHandler> fileHandlers;

	public FileHandlerFactory(List<FileHandler> fileHandlers) {
		this.fileHandlers = fileHandlers;
	}

	public FileHandler getFileHandler(String fileName) {
		String extension = getFileExtension(fileName);
		return fileHandlers.stream()
			.filter(handler -> handler.supports(extension))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원되지 않는 파일 형식입니다."));
	}

	public String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
