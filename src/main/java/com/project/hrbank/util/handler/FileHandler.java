package com.project.hrbank.util.handler;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.entity.FileEntity;

public interface FileHandler {

	/**
	 *
	 * @param extension 파일 확장자
	 * @return 지원 여부
	 */
	boolean supports(String extension);

	/**
	 * 파일 처리 후 가공된 파일 데이터 반환
	 * @param file  업로드 할 파일
	 * @return 처리된 파일의 바이트 배열
	 * @throws IOException 파일 처리 중 오류 발생
	 */
	byte[] processMultipartFile(MultipartFile file) throws IOException;

	/**
	 *
	 * @param fileData 업로드 할 파일 데이터
	 * @return 처리된 파일의 바이트 배열
	 * @throws IOException 파일 처리 중 오류 발생
	 */
	byte[] processFileData(String fileName, byte[] fileData) throws IOException;

	/**
	 * @param filePath 파일 경로
	 * @param fileName 파일명
	 * @return 파일 다운로드 응답 객체
	 * @throws IOException 파일 처리 중 오류 발생 가능
	 */
	FileEntity handleDownload(Path filePath, String fileName) throws IOException;

}
