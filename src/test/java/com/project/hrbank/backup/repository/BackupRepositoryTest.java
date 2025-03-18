package com.project.hrbank.backup.repository;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.config.JpaAuditing;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaAuditing.class})
class BackupRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BackupRepository backupRepository;

    @Test
    void writeHereTestName() {
        // given
        Backup createBackup = new Backup("test", Status.IN_PROGRESS);
        Backup save = backupRepository.save(createBackup);

        entityManager.flush();
        // when
        Backup backup = backupRepository.findLastBackup2().orElseThrow(() -> new RuntimeException("No Entity"));

        // then
    }
}
