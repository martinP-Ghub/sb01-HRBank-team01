package com.project.hrbank.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.hrbank.entity.Employee;
import com.project.hrbank.entity.enums.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByEmail(String email);

	long countByStatus(EmployeeStatus status);

	Page<Employee> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);

	@Query("SELECT COUNT(e) FROM Employee e WHERE " +
		"(:status IS NULL OR e.status = :status) AND " +
		"(:fromDate IS NULL OR e.hireDate >= :fromDate) AND " +
		"(:toDate IS NULL OR e.hireDate <= :toDate)")
	long countEmployees(
		@Param("status") EmployeeStatus status,
		@Param("fromDate") LocalDate fromDate,
		@Param("toDate") LocalDate toDate
	);

	long countByHireDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("SELECT e FROM Employee e WHERE " +
		"(:departmentName IS NULL OR e.departmentId = :departmentName) AND " +
		"(:position IS NULL OR e.position = :position) AND " +
		"(:status IS NULL OR e.status = :status)")
	Page<Employee> findFilteredEmployees(
		@Param("departmentName") String departmentName,
		@Param("position") String position,
		@Param("status") EmployeeStatus status,
		Pageable pageable
	);

	@Query("SELECT COUNT(e) FROM Employee e WHERE e.hireDate = CURRENT_DATE")
	long countEmployeesForToday();

	@Query("SELECT COUNT(e) FROM Employee e WHERE YEAR(e.hireDate) = YEAR(CURRENT_DATE) AND WEEK(e.hireDate) = WEEK(CURRENT_DATE)")
	long countEmployeesForCurrentWeek();

	@Query("SELECT COUNT(e) FROM Employee e WHERE YEAR(e.hireDate) = YEAR(CURRENT_DATE) AND MONTH(e.hireDate) = MONTH(CURRENT_DATE)")
	long countEmployeesForCurrentMonth();

	@Query("SELECT COUNT(e) FROM Employee e WHERE YEAR(e.hireDate) = YEAR(CURRENT_DATE) AND CEIL(MONTH(e.hireDate)/3.0) = CEIL(MONTH(CURRENT_DATE)/3.0)")
	long countEmployeesForCurrentQuarter();

	@Query("SELECT COUNT(e) FROM Employee e WHERE YEAR(e.hireDate) = YEAR(CURRENT_DATE)")
	long countEmployeesForCurrentYear();

	@Query("SELECT COUNT(e) FROM Employee e WHERE e.departmentId = :departmentId")
	long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

	@Query("SELECT d.name AS departmentName, COUNT(e) AS count " +
		"FROM Employee e JOIN Department d ON e.departmentId = d.id " +
		"WHERE e.status = :status " +
		"GROUP BY d.name")
	List<Object[]> countEmployeesGroupedByDepartment(@Param("status") EmployeeStatus status);

	@Query("SELECT e.position AS positionName, COUNT(e) AS count " +
		"FROM Employee e " +
		"WHERE e.status = :status " +
		"GROUP BY e.position")
	List<Object[]> countEmployeesGroupedByPosition(@Param("status") EmployeeStatus status);

}
