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

	@Query("SELECT e FROM EmployeeLogs e " +
		"WHERE (COALESCE(:cursor, e.changedAt) = e.changedAt OR e.changedAt < :cursor) " +
		"AND (COALESCE(:atFrom, e.changedAt) = e.changedAt OR COALESCE(:atTo, e.changedAt) = e.changedAt OR e.changedAt BETWEEN :atFrom AND :atTo) "
		+
		"AND (:employeeNumber IS NULL OR e.employeeNumber LIKE CONCAT('%', :employeeNumber, '%')) " +
		"AND (:memo IS NULL OR e.memo LIKE CONCAT('%', :memo, '%')) " +
		"AND (:ipAddress IS NULL OR e.ipAddress LIKE CONCAT('%', :ipAddress, '%')) " +
		"AND (:type IS NULL OR e.type LIKE CONCAT('%', :type, '%'))")
	Slice<EmployeeLogs> findAll(
		@Param("cursor") LocalDateTime cursor,
		@Param("employeeNumber") String employeeNumber,
		@Param("memo") String memo,
		@Param("ipAddress") String ipAddress,
		@Param("type") String type,
		@Param("atFrom") LocalDateTime atFrom,
		@Param("atTo") LocalDateTime atTo,
		Pageable pageable
	);

}
