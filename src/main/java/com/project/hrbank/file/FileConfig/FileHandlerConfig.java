package com.project.hrbank.file.FileConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.hrbank.file.handler.FileHandler;
import com.project.hrbank.file.handler.impl.CsvFileHandler;
import com.project.hrbank.file.handler.impl.ImageFileHandler;
import com.project.hrbank.file.handler.impl.LogFileHandler;

@Configuration
public class FileHandlerConfig {
	@Bean
	public FileHandler csvFileHandler() {
		return new CsvFileHandler();
	}

	@Bean
	public FileHandler imageFileHandler() {
		return new ImageFileHandler();
	}

	@Bean
	public FileHandler logFileHandler() {
		return new LogFileHandler();
	}
}
