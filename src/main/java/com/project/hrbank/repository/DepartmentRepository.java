package com.project.hrbank.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hrbank.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
	boolean existsByName(String name);

	@Query("SELECT d FROM Department d " +
		"WHERE (COALESCE(:cursor, d.createdAt) = d.createdAt OR d.createdAt > :cursor) " +
		"AND ((LOWER(d.name) = LOWER(COALESCE(:search, d.name)) OR LOWER(d.description) = LOWER(COALESCE(:search, d.description))) OR :search IS NULL) "
		+
		"ORDER BY d.createdAt, d.name")
	Slice<Department> findNextDepartments(
		@Param("cursor") LocalDateTime cursor,
		@Param("search") String search,
		Pageable pageable);
}
