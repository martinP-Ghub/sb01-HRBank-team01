package com.project.hrbank.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.project.hrbank.dto.DepartmentDto;

public record CursorPageResponseDepartmentDto(
	List<DepartmentDto> content,
	LocalDateTime nextCursor,
	Long nextIdAfter,
	int size,
	boolean hasNext,
	Long totalElements
) {

}
