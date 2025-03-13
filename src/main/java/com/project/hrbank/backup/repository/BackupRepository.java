package com.project.hrbank.backup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hrbank.backup.domain.Backup;

public interface BackupRepository extends JpaRepository<Backup, Long> {
}
