package com.project.hrbank.repository;

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

	@Query("SELECT d FROM Department d WHERE " +
		"LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		"LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Department> searchDepartments(@Param("search") String search, Pageable pageable);
}
