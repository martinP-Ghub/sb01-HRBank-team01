package com.project.hrbank.backup.dto.response;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseBackupDto(
	List<BackupDto> content,
	Instant nextCursor,
	Long nextIdAfter,
	int size,
	boolean hasNext,
	Long totalElements
) {

}
