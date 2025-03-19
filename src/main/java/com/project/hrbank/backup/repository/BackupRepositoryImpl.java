package com.project.hrbank.backup.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BackupRepositoryImpl {

	private final EntityManager entityManager;

	public Page<Backup> findWithSearchCondition(LocalDateTime cursor, Status status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		String jpql = "SELECT b FROM Backup b";
		String whereSql = " WHERE ";
		List<String> whereCondition = new ArrayList<>();

		// 조건 추가
		if (cursor != null) {
			whereCondition.add("b.createdAt < :cursor");
		}
		if (status != null) {
			whereCondition.add("b.status = :status");
		}
		if (startDate != null) {
			whereCondition.add("b.startedAt >= :startDate");
		}
		if (endDate != null) {
			whereCondition.add("b.endedAt <= :endDate");
		}

		// WHERE 절 추가
		if (!whereCondition.isEmpty()) {
			jpql += whereSql + String.join(" AND ", whereCondition);
		}

		// 정렬 추가
		jpql += " ORDER BY b.endedAt DESC";

		// JPQL 실행
		TypedQuery<Backup> query = entityManager.createQuery(jpql, Backup.class);

		// 파라미터 바인딩
		if (cursor != null) {
			query.setParameter("cursor", cursor);
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}

		// 페이징 적용
		List<Backup> backups = query
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		// 전체 개수 조회
		long totalCount = getTotalCount(cursor, status, startDate, endDate);

		return new PageImpl<>(backups, pageable, totalCount);
	}

	private long getTotalCount(LocalDateTime cursor, Status status, LocalDateTime startDate, LocalDateTime endDate) {
		String countJpql = "SELECT COUNT(b) FROM Backup b";
		String whereSql = " WHERE ";
		List<String> whereCondition = new ArrayList<>();

		if (cursor != null) {
			whereCondition.add("b.createdAt < :cursor");
		}
		if (status != null) {
			whereCondition.add("b.status = :status");
		}
		if (startDate != null) {
			whereCondition.add("b.startedAt >= :startDate");
		}
		if (endDate != null) {
			whereCondition.add("b.endedAt <= :endDate");
		}

		if (!whereCondition.isEmpty()) {
			countJpql += whereSql + String.join(" AND ", whereCondition);
		}

		TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);

		if (cursor != null) {
			countQuery.setParameter("cursor", cursor);
		}
		if (status != null) {
			countQuery.setParameter("status", status);
		}
		if (startDate != null) {
			countQuery.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			countQuery.setParameter("endDate", endDate);
		}

		return countQuery.getSingleResult();
	}
}

