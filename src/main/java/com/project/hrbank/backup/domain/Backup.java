package com.project.hrbank.backup.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Backup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "backup_id")
	private Long id;

	@Column(name = "worker", nullable = false)
	private String worker;

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// file_id 연관관계 맺기 1..0 : 1 여기가 주인임!

	public Backup(String worker, Status status) {
		this.worker = worker;
		this.status = status;
		this.startedAt = LocalDateTime.now();
	}

}
