package com.project.hrbank.dto;

import com.project.hrbank.entity.FileEntity;

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
