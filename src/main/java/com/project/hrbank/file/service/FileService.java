package com.project.hrbank.file.service;

import java.util.Optional;

import org.springframework.core.io.Resource;

import com.project.hrbank.file.entity.FileEntity;

public interface FileService {

	FileEntity find(Long id);
}
