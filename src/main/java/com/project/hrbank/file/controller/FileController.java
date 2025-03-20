package com.project.hrbank.file.controller;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
		InputStream fileStream = fileStorage.getFileStream(fileEntity.getId());

		InputStreamResource fileResource = new InputStreamResource(fileStream);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
			.contentLength(fileEntity.getSize())
			.contentType(MediaType.parseMediaType(fileEntity.getContentType()))
			.body(fileResource);

	}

	@PostMapping("/upload")
	public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
		return ResponseEntity.ok(fileService.saveMultipartFile(file));
	}

	@GetMapping("/{id}")
	public ResponseEntity<FileEntity> getFile(@PathVariable Long id) {
		return ResponseEntity.ok(fileService.find(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFile(@PathVariable Long id) {
		fileService.deleteFile(id);
		return ResponseEntity.ok("파일 삭제 완료");
	}
}
