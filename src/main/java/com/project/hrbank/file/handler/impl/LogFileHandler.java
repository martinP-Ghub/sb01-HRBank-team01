package com.project.hrbank.file.handler.impl;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.file.handler.FileHandler;

public class LogFileHandler implements FileHandler {

	@Override
	public boolean supports(String extension) {
		return false;
	}

	@Override
	public void processFile(MultipartFile file) throws IOException {

	}

	@Override
	public Resource handleDownload(Resource fileResource, String fileName) throws IOException {
		return null;
	}
}
