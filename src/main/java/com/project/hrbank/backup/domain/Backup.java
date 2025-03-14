package com.project.hrbank.backup.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "backups")
public class Backup {

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * 작업자
	 *시작 시간
	 *종료 시간
	 *상태 (진행중, 완료, 실패, 건너뜀)
	 *백업 파일
	 */
}
