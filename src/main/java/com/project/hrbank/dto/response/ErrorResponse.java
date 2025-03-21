package com.project.hrbank.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String message, String details) {

	public static ErrorResponse of(int status, String message, String details) {
		return new ErrorResponse(LocalDateTime.now(), status, message, details);
	}
}
