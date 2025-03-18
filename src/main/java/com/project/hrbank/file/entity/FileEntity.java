package com.project.hrbank.file.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id")
	private Long id;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "content_type", nullable = false)
	private String contentType;

	@Column(name = "size", nullable = false)
	private Long size;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public FileEntity(Long id, String fileName, String contentType, Long size, String filePath) {
		this.id = id;
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
		this.filePath = filePath;
		this.createdAt = LocalDateTime.now();
	}

}
