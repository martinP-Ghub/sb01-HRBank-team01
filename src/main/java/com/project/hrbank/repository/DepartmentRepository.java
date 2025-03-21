package com.project.hrbank.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hrbank.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	boolean existsByName(String name);

	@Query("SELECT d FROM Department d " +
		"WHERE (COALESCE(:cursor, d.createdAt) = d.createdAt OR d.createdAt < :cursor) " +
		"AND (:nameOrDescription IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :nameOrDescription, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :nameOrDescription, '%'))) ")
	Page<Department> findNextDepartments(
		@Param("cursor") LocalDateTime cursor,
		@Param("nameOrDescription") String nameOrDescription,
		Pageable pageable
	);
}
