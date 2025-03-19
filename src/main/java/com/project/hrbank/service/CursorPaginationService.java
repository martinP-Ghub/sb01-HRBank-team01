package com.project.hrbank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.project.hrbank.dto.response.CursorPageResponse;

@Service
public class CursorPaginationService {

	public <T, R> CursorPageResponse<R> getPaginatedResults(
		LocalDateTime cursor,
		Pageable pageable,
		JpaRepository<T, Long> repository,
		Function<T, R> mapper,
		Function<T, LocalDateTime> cursorExtractor,
		Function<T, Long> idExtractor,
		BiFunction<LocalDateTime, Pageable, Slice<T>> findMethod
	) {
		// 기본값 설정
		cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());

		// 데이터 조회
		Slice<T> slice = findMethod.apply(cursor, pageable);

		// DTO 변환
		List<R> content = slice.getContent().stream().map(mapper).toList();

		// 다음 커서 값 계산
		LocalDateTime nextCursor = !slice.getContent().isEmpty()
			? cursorExtractor.apply(slice.getContent().get(slice.getContent().size() - 1))
			: null;

		// 다음 ID 계산
		Long nextIdAfter = (slice.hasNext() && !content.isEmpty())
			? idExtractor.apply(slice.getContent().get(slice.getContent().size() - 1))
			: null;

		long count = repository.count();

		return new CursorPageResponse<>(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(), count);
	}
}
