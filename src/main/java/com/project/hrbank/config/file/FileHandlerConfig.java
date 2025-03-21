package com.project.hrbank.config.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.hrbank.util.handler.FileHandler;
import com.project.hrbank.util.handler.impl.CsvFileHandler;
import com.project.hrbank.util.handler.impl.ImageFileHandler;
import com.project.hrbank.util.handler.impl.LogFileHandler;

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
