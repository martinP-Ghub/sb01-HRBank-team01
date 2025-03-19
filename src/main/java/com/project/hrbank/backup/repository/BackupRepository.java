package com.project.hrbank.backup.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;

public interface BackupRepository extends JpaRepository<Backup, Long> {

	@Query("select b from Backup b order by b.endedAt DESC LIMIT 1")
	Optional<Backup> findLastBackup();

	@Query(
		"SELECT b FROM Backup b left join fetch b.file "
			+ "WHERE b.startedAt < :cursor "
			+ "AND (:status IS NULL OR b.status = :status) "
			+ "AND b.startedAt >= :startedAtFrom "
			+ "AND b.startedAt <= :startedAtTo"
	)
	Page<Backup> findAllBy(
		@Param("cursor") Instant cursor,
		@Param("status") Status status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		Pageable pageable
	);

	@Query("SELECT COUNT(b) FROM Backup b WHERE (:status IS NULL OR b.status = :status)")
	long countBackups(@Param("status") Status status);

}
