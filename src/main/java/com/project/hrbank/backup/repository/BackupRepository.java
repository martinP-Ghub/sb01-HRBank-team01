package com.project.hrbank.backup.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;

public interface BackupRepository extends JpaRepository<Backup, Long> {

	@Query("select b from Backup b order by b.endedAt DESC LIMIT 1")
	Optional<Backup> findLastBackup();

	@Query("SELECT b FROM Backup b left join fetch b.file WHERE b.createdAt < :cursor")
	Slice<Backup> findAllBy(@Param("cursor") LocalDateTime cursor, Pageable pageable);

	@Query("SELECT COUNT(b) FROM Backup b WHERE (:status IS NULL OR b.status = :status)")
	long countBackups(@Param("status") Status status);

}
