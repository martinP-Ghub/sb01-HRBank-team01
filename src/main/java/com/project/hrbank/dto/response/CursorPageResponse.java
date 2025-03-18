package com.project.hrbank.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponse<T>(
	List<T> content,
	LocalDateTime nextCursor,
	Long nextIdAfter,
	int size,
	boolean hasNext,
	long totalElements
) {
}
