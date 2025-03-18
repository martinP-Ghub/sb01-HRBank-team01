package com.project.hrbank.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.hrbank.file.dto.FileDto;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.service.FileService;
import com.project.hrbank.file.storage.FileStorage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;
	private final FileStorage fileStorage;

	@GetMapping("/{id}/download")
	public ResponseEntity<?> download(@PathVariable Long id) {
		FileEntity fileEntity = fileService.find(id);
		return fileStorage.download(FileDto.fromEntity(fileEntity));
	}
}
