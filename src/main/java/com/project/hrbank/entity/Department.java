package com.project.hrbank.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private LocalDate establishedDate;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public Department(String name, String description, LocalDate establishedDate) {
		this.name = name;
		this.description = description;
		this.establishedDate = establishedDate;
	}

	public Department(Long id, String name, String description, LocalDate establishedDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.establishedDate = establishedDate;
	}
}


