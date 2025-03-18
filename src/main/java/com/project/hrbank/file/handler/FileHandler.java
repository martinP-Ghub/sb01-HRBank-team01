package com.project.hrbank.file.handler;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.file.entity.FileEntity;

public interface FileHandler {

	/**
	 *
	 * @param extension 파일 확장자
	 * @return 지원 여부
	 */
	boolean supports(String extension);

	/**
	 *
	 * @param file 업로드할 파일
	 * @throws IOException 파일 처리 중 오류 발생
	 */
	void processFile(MultipartFile file) throws IOException;

	/**
	 * @param filePath 파일 경로
	 * @param fileName 파일명
	 * @return 파일 다운로드 응답 객체
	 * @throws IOException 파일 처리 중 오류 발생 가능
	 */
	FileEntity handleDownload(Path filePath, String fileName) throws IOException;

}
