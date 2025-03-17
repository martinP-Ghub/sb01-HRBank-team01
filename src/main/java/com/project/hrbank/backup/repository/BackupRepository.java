package com.project.hrbank.backup.repository;

import com.project.hrbank.backup.domain.Backup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BackupRepository extends JpaRepository<Backup, Long> {

    @Query(value = "SELECT b FROM Backup b ORDER BY b.endedAt DESC ")
    List<Backup> findLastBackup(Pageable pageable);

    @Query("select b from Backup b order by b.endedAt DESC LIMIT 1")
    Optional<Backup> findLastBackup2();

}
