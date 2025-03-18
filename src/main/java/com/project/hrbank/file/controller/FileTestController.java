package com.project.hrbank.file.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.service.FileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test/files")
@RequiredArgsConstructor
public class FileTestController {

	private final FileService fileService;

	@PostMapping("/upload")
	public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
		return ResponseEntity.ok(fileService.saveFile(file));
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