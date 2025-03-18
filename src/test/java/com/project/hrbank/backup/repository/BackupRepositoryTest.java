package com.project.hrbank.backup.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.config.JpaAuditingConfiguration;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import({JpaAuditingConfiguration.class})
class BackupRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BackupRepository backupRepository;

	@Test
	void writeHereTestName() {
		// given
		Backup createBackup = Backup.ofSystem();
		Backup save = backupRepository.save(createBackup);

		entityManager.flush();
		// when
		Backup backup = backupRepository.findLastBackup().orElseThrow(() -> new RuntimeException("No Entity"));

		// then
	}
}
