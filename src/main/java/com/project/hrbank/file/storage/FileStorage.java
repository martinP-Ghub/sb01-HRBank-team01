package com.project.hrbank.file.storage;

import java.io.InputStream;

import com.project.hrbank.file.dto.FileDto;
import com.project.hrbank.file.entity.FileEntity;

public interface FileStorage {

	/**
	 *
	 * @param id 파일 ID (없다면 null 가능)
	 * @param fileData 저장할 파일 데이터
	 * @param fileName 저장할 파일 이름
	 * @param contentType 저장할 파일 타입
	 * @return 파일 메타데이터(FileEntity)
	 */
	FileEntity saveFile(Long id, byte[] fileData, String fileName, String contentType);

	/**
	 * 저장된 파일을 가져오기
	 * @param id 파일의 ID
	 * @return 파일 데이터의 InputStream
	 */
	InputStream get(Long id);

	/**
	 *
	 * @param id 가져올 file id
	 * @return file InputStream 으로 가져오기
	 */
	InputStream getFileStream(Long id);

	/**
	 * 파일 삭제
	 * @param id 파일의 ID
	 * @return 삭제 성공 여부
	 */
	boolean delete(Long id);
}
