package com.project.hrbank.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	boolean existsByEmail(String email);

	boolean existsByEmailAndEmployeeIdNot(String email, Long employeeId);

	Page<Employee> findByEmployeeIdGreaterThan(Long employeeId, Pageable pageable);

	long countByStatusIn(List<EmployeeStatus> statuses);
}

