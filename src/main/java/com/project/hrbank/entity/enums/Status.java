package com.project.hrbank.entity.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {

	IN_PROGRESS("진행중"),
	COMPLETED("완료"),
	FAILED("실패"),
	SKIPPED("건너뜀"),
	UNKNOWN("알 수 없음");

	private final String description;

}
