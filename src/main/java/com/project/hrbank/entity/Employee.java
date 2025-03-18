package com.project.hrbank.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private Long employeeId;

	@Column(name = "employee_number", nullable = false, unique = true)
	private String employeeNumber;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "department_id", nullable = false)
	private Long departmentId;

	@Column(name = "position", nullable = false)
	private String position;

	@Column(name = "hire_date", nullable = false)
	private LocalDate hireDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EmployeeStatus status;

	@Column(name = "profile_image_id")
	private Long profileImageId;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		status = EmployeeStatus.ACTIVE;
	}
}

