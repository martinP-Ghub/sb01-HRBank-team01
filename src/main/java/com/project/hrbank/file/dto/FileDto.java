package com.project.hrbank.file.dto;

import com.project.hrbank.file.entity.FileEntity;

public record FileDto(Long id, String fileName, String contentType, Long size) {

	public static FileDto fromEntity(FileEntity fileEntity) {
		return new FileDto(
			fileEntity.getId(),
			fileEntity.getFileName(),
			fileEntity.getContentType(),
			fileEntity.getSize()
		);
	}

}
