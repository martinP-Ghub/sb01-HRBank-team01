package com.project.hrbank.file.service;

import org.springframework.stereotype.Service;

import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	private final FileRepository fileRepository;

	@Override
	public FileEntity find(Long id) {
		return null;
	}
}
