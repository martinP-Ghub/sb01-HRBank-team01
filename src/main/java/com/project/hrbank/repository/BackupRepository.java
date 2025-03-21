package com.project.hrbank.repository;

import com.project.hrbank.entity.Backup;
import com.project.hrbank.entity.enums.Status;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackupRepository extends JpaRepository<Backup, Long> {

    @Query(value = "SELECT * FROM backups ORDER BY ended_at DESC LIMIT 1", nativeQuery = true)
    Optional<Backup> findLastBackup();

    @Query(
            "SELECT b FROM Backup b left join fetch b.file "
                    + "WHERE b.startedAt < :cursor "
                    + "AND (:status IS NULL OR b.status = :status) "
                    + "AND b.startedAt >= :startedAtFrom "
                    + "AND b.startedAt <= :startedAtTo "
                    + "AND (:worker IS NULL OR b.worker LIKE :worker) "
    )
    Page<Backup> findAllBy(
            @Param("cursor") LocalDateTime cursor,
            @Param("status") Status status,
            @Param("startedAtFrom") LocalDateTime startedAtFrom,
            @Param("startedAtTo") LocalDateTime startedAtTo,
            @Param("worker") String worker,
            Pageable pageable
    );

    @Query("SELECT COUNT(b) FROM Backup b WHERE (:status IS NULL OR b.status = :status)")
    long countBackups(@Param("status") Status status);

}
