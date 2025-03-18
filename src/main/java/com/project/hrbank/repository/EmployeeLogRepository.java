package com.project.hrbank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hrbank.entity.EmployeeLogs;

@Repository
public interface EmployeeLogRepository extends JpaRepository<EmployeeLogs, Long> {
	List<EmployeeLogs> findAll(Sort sort);

	boolean existsByChangedAtAfter(LocalDateTime lastBackupEndedAt);

}
