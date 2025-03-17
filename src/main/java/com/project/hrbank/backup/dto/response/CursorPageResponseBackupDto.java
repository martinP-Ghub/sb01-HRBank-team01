package com.project.hrbank.backup.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseBackupDto(
	List<BackupDto> content,
	LocalDateTime nextCursor,
	Long nextIdAfter,
	int size,
	boolean hasNext,
	Long totalElements
) {

}
