package com.project.hrbank.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_change_logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "log_id");
	private Long log_id;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String memo;

	@Column(nullable = false)
	private String ip;

	@Column(name = "changed_at")
	private LocalDateTime changedAt;

	@Column(name = "changed_value", columnDefinition = "jsonb")
	private String changedValue;

	@Column(name = "employee_number", updatable = true)
	private String employeeNumber;

}
