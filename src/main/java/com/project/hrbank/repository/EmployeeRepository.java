package com.project.hrbank.repository;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.EmployeeStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByEmail(String email);

	long countByStatus(EmployeeStatus status);

	Page<Employee> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);

}
