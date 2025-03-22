package com.project.hrbank.service;

import com.project.hrbank.dto.response.BackupResponse;
import com.project.hrbank.dto.response.CursorPageResponse;
import com.project.hrbank.entity.Backup;
import com.project.hrbank.entity.enums.Status;
import com.project.hrbank.repository.BackupRepository;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.util.provider.EmployeesLogCsvFileProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
    private static final LocalDateTime POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(1, 1, 1, 0, 0);
    private static final String SYSTEM_NAME = "SYSTEM";

    private final BackupRepository backupRepository;
    private final EmployeeLogRepository employeeLogRepository;
    private final EmployeesLogCsvFileProvider csvProvider;

    public CursorPageResponse<BackupResponse> findAll(
            LocalDateTime cursor,
            Status status,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            String worker,
            Pageable pageable
    ) {
        cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());
        startedAtFrom = Optional.ofNullable(startedAtFrom).orElse(POSTGRESQL_MIN_TIMESTAMP);
        startedAtTo = Optional.ofNullable(startedAtTo).orElse(LocalDateTime.now());

        Page<Backup> page = backupRepository.findAllBy(cursor, status, startedAtFrom, startedAtTo, worker, pageable);

        List<BackupResponse> content = getBackupContents(page);

        LocalDateTime nextCursor = null;
        if (page.hasContent()) {
            nextCursor = content.get(content.size() - 1).startedAt();
        }

        Long nextIdAfter = null;
        if (page.hasNext() && page.hasContent()) {
            nextIdAfter = content.get(content.size() - 1).id();
        }

        return new CursorPageResponse<BackupResponse>(content, nextCursor, nextIdAfter, content.size(), page.hasNext(),
                page.getTotalElements());
    }

    private List<BackupResponse> getBackupContents(Page<Backup> slice) {
        return slice.getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void backupBySystem() {
        backup(SYSTEM_NAME);
    }

    @Transactional
    public BackupResponse backup(String clientIpAddr) {

        Backup backup = generateBackup(clientIpAddr);

        LocalDateTime lastEndedAtBackupDateTime = getLastEndedAt();
        if (isNotChangedEmployeeInfo(lastEndedAtBackupDateTime)) {
            backup.updateSkipped();
            return toDto(backupRepository.save(backup));
        }

        generateBackupFile(backup);
        backupRepository.save(backup);
        return toDto(backup);
    }

    private Backup generateBackup(String clientIpAddr) {
        return Backup.ofInProgress(clientIpAddr);
    }

    private LocalDateTime getLastEndedAt() {

        return backupRepository.findLastBackup().stream()
                .findFirst()
                .map(Backup::getEndedAt)
                .orElse(POSTGRESQL_MIN_TIMESTAMP);
    }

    private boolean isNotChangedEmployeeInfo(LocalDateTime endedAt) {
        return !employeeLogRepository.existsByChangedAtAfter(endedAt);
    }

    private void generateBackupFile(Backup backup) {
        csvProvider.saveEmployeeLogFile(backup.getId())
                .ifPresentOrElse(
                        backup::updateCompleted,
                        backup::updateFailed
                );
    }

    public BackupResponse findLatest() {
        Backup backup = getLastBackup();
        return toDto(backup);
    }

    private Backup getLastBackup() {
        return backupRepository.findLastBackup()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private BackupResponse toDto(Backup backup) {
        return BackupResponse.toDto(backup);
    }

}
