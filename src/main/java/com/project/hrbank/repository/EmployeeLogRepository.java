package com.project.hrbank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hrbank.entity.EmployeeLogs;

@Repository
public interface EmployeeLogRepository extends JpaRepository<EmployeeLogs, Long> {
	List<EmployeeLogs> findAll(Sort sort);

	boolean existsByChangedAtAfter(LocalDateTime lastBackupEndedAt);

	@Query("SELECT e FROM EmployeeLogs e WHERE e.changedAt < :cursor")
	Slice<EmployeeLogs> findAllBy(@Param("cursor") LocalDateTime cursor, Pageable pageable);

	// @Query("""
	// 	   SELECT e FROM EmployeeLogs e
	// 	   WHERE (:cursor IS NULL OR e.logId > :cursor)
	// 	   ORDER BY e.logId ASC
	// 	   LIMIT :size
	// 	""")
	// List<EmployeeLogs> findLogsByCursor(@Param("cursor") Long cursor, @Param("size") int size);
}
