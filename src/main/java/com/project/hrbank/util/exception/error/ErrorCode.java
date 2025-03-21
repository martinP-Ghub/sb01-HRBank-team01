package com.project.hrbank.util.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	;
	private final String message;
	private final String details;
}
