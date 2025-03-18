package com.project.hrbank.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hrbank.entity.EmployeeLogs;

@Repository
public interface EmployeeLogRepository extends JpaRepository<EmployeeLogs, Long> {
	boolean existsByChangedAtAfter(LocalDateTime lastBackupEndedAt);

	@Query("SELECT e FROM EmployeeLogs e WHERE e.changedAt < :cursor")
	Slice<EmployeeLogs> findAll(@Param("cursor") LocalDateTime cursor, Pageable pageable);

}
