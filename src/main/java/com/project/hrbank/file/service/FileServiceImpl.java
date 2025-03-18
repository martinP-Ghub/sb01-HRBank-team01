package com.project.hrbank.file.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.file.FileHandlerFactory;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.handler.FileHandler;
import com.project.hrbank.file.repository.FileRepository;
import com.project.hrbank.file.storage.FileStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	private final FileRepository fileRepository;
	private final FileStorage fileStorage;
	private final FileHandlerFactory fileHandlerFactory;

	@Override
	public FileEntity saveFile(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("업로드 파일이 비어있습니다.");
		}

		String fileName = (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "unknown_file";
		FileHandler fileHandler = fileHandlerFactory.getFileHandler(fileName);
		byte[] processedFileData = fileHandler.processFile(file);

		FileEntity fileEntity = fileStorage.saveFile(
			null,
			processedFileData,
			file.getOriginalFilename(),
			file.getContentType()
		);
		return fileRepository.save(fileEntity);
	}

	@Override
	public FileEntity find(Long fileId) {
		return fileRepository.findById(fileId)
				.orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileId));
	}

	@Override
	public FileEntity updateFile(Long fileId, MultipartFile newFile) throws IOException {
		FileEntity existFile = find(fileId);
		fileStorage.delete(existFile.getId());

		FileEntity updateFile = fileStorage.saveFile(
			null,
			newFile.getBytes(),
			newFile.getOriginalFilename(),
			newFile.getContentType()
		);
		return fileRepository.save(updateFile);
	}

	@Override
	public void deleteFile(Long fileId) {
		FileEntity findEntity = find(fileId);
		fileStorage.delete(findEntity.getId());
		fileRepository.delete(findEntity);
	}

}
