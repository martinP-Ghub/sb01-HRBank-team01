package com.project.hrbank.util.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.hrbank.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
		int status = HttpStatus.NOT_FOUND.value();
		ErrorResponse errorResponse = ErrorResponse.of(status, "잘못된 요청입니다.", "~는 필수입니다.");

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(NoSuchElementException.class)
	protected ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException exception) {
		int status = HttpStatus.NOT_FOUND.value();
		ErrorResponse errorResponse = ErrorResponse.of(status, "잘못된 요청입니다.", "리소스를 찾을 수 업습니다.");

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		ErrorResponse errorResponse = ErrorResponse.of(status, "잘못된 요청입니다.", "리소스를 찾을 수 업습니다.");
		return ResponseEntity.internalServerError().body(errorResponse);
	}

}
