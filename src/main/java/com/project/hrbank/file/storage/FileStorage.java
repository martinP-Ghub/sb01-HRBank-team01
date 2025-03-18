package com.project.hrbank.file.storage;

import java.io.InputStream;

import org.springframework.http.ResponseEntity;

import com.project.hrbank.file.dto.FileDto;

public interface FileStorage {

	/**
	 * 파일을 저장소에 저장
	 * @param id 파일 ID (없다면 null 가능)
	 * @param fileData 저장할 파일 데이터
	 * @return 저장된 파일의 ID
	 */
	Long put(Long id, byte[] fileData);

	/**
	 * 저장된 파일을 가져오기
	 * @param id 파일의 ID
	 * @return 파일 데이터의 InputStream
	 */
	InputStream get(Long id);

	/**
	 * 파일 다운로드 응답 생성
	 * @param fileDto 다운로드할 파일의 메타 데이터
	 * @return 다운로드 ResponseEntity
	 */
	ResponseEntity<?> download(FileDto fileDto);

	/**
	 * 파일 삭제
	 * @param id 파일의 ID
	 * @return 삭제 성공 여부
	 */
	boolean delete(Long id);
}
