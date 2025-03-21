package com.project.hrbank.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.project.hrbank.entity.FileEntity;

public interface FileService {

	/**
	 * 파일을 저장하고 메타 정보 반환
	 * @param file 업도르할 파일
	 * @return 저장된 파일의 엔티티 정보
	 * @throws IOException 파일 저장 중 오류 발생 시 예외 처리
	 */
	FileEntity saveMultipartFile(MultipartFile file) throws IOException;

	/**
	 * @param fileName    파일 이름
	 * @param fileData    파일 바이트 데이터
	 * @param contentType 파일 확장자 타입
	 * @return 저장된 파일의 엔티티 정보
	 * @throws IOException 파일 저장 중 오류 발생 시 예외 처리
	 */
	FileEntity saveFileData(String fileName, byte[] fileData, String contentType) throws IOException;

	/**
	 * 파일을 조회하고 엔티티 정보 반환
	 * @param fileId 파일 ID
	 * @return 조회된 파일 엔티티
	 */
	FileEntity find(Long fileId);

	/**
	 * 파일을 업데이트 하고 변경된 메타 정보 반환
	 * @param fileId 기존 파일 ID
	 * @param newFile 새롭게 업로드할 파일
	 * @return 업데이트된 파일 엔티티 정보
	 * @throws IOException 파일 업데이트 중 오류 발생 시 예외 처리
	 */
	FileEntity updateFile(Long fileId, MultipartFile newFile) throws IOException;

	/**
	 * 파일 삭제
	 * @param fileId 파일 ID
	 */
	void deleteFile(Long fileId);

}
