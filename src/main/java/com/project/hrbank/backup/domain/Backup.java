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

}
