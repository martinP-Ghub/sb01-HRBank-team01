package com.project.hrbank.backup.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import com.project.hrbank.entity.base.BaseTimeEntity;
import com.project.hrbank.file.entity.FileEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "backups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backup extends BaseTimeEntity {

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

	@OneToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
	@JoinColumn(name = "file_id")
	FileEntity file;

	public Backup(String worker, Status status, LocalDateTime startedAt, LocalDateTime endedAt) {
		this.worker = worker;
		this.status = status;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
	}

	public static Backup ofSystem() {
		Status status = Status.COMPLETED;
		return new Backup("system", status, LocalDateTime.now(), LocalDateTime.now());
	}

	public static Backup ofInProgress(String clientIpAddr) {
		Status status = Status.IN_PROGRESS;
		return new Backup(clientIpAddr, status, LocalDateTime.now(), null);
	}

	public void updateSkipped() {
		this.endedAt = LocalDateTime.now();
		this.status = Status.SKIPPED;
	}

	public void updateFailed() {
		this.endedAt = LocalDateTime.now();
		this.status = Status.FAILED;
	}

	public void updateCompleted(FileEntity file) {
		this.endedAt = LocalDateTime.now();
		this.status = Status.COMPLETED;
		this.file = file;
	}

	public Long getFileId() {
		return Optional.ofNullable(this.file)
			.map(FileEntity::getId)
			.orElse(null);
	}

}
