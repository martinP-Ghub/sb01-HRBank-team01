package com.project.hrbank.backup.service;

import com.project.hrbank.backup.domain.Backup;
import com.project.hrbank.backup.domain.Status;
import com.project.hrbank.backup.dto.response.BackupDto;
import com.project.hrbank.backup.dto.response.CursorPageResponseBackupDto;
import com.project.hrbank.backup.provider.CsvProvider;
import com.project.hrbank.backup.repository.BackupRepository;
import com.project.hrbank.backup.repository.BackupRepositoryImpl;
import com.project.hrbank.file.entity.FileEntity;
import com.project.hrbank.file.storage.FileStorage;
import com.project.hrbank.repository.EmployeeLogRepository;
import com.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {
    private static final LocalDateTime POSTGRESQL_MIN_TIMESTAMP = LocalDateTime.of(4713, 11, 24, 0, 0);
    public static final String SYSTEM_NAME = "SYSTEM";

    private final BackupRepository backupRepository;
    private final EmployeeLogRepository employeeLogRepository;
    private final BackupRepositoryImpl backupRepositoryImpl;
    private final FileStorage fileStorage;
    private final EmployeeRepository employeeRepository;
    private final CsvProvider csvProvider;

    public CursorPageResponseBackupDto findAll(LocalDateTime cursor, Pageable pageable) {
        cursor = Optional.ofNullable(cursor).orElse(LocalDateTime.now());
        Slice<Backup> slice = backupRepository.findAllBy(cursor, pageable);

        List<BackupDto> content = getBackupContents(slice);

        LocalDateTime nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
        }

        Long nextIdAfter = null;
        if (slice.hasNext()) {
            nextIdAfter = content.get(content.size() - 1).id();
        }

        long count = backupRepository.count();
        return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(),
                count);
    }

    private List<BackupDto> getBackupContents(Slice<Backup> slice) {
        return slice.getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void backupBySystem() {
        backup(SYSTEM_NAME);
    }

    @Transactional
    public BackupDto backup(String clientIpAddr) {
        LocalDateTime lastEndedAtBackupDateTime = getLastEndedAt();

        Backup backup = Backup.ofInProgress(clientIpAddr);
        backupRepository.save(backup);

        if (isNotChangedEmployeeInfo(lastEndedAtBackupDateTime)) {
            backup.updateSkipped();
            return toDto(backupRepository.save(backup));
        }

        try {
            FileEntity generateBackupFile = generateBackupFile();
            backup.updateCompleted(generateBackupFile);
        } catch (RuntimeException exception) {
            backup.updateFailed();
            // 로그 파일에 데이터 쓰기
        }
        return toDto(backup);
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

    private FileEntity generateBackupFile() {
        String fileName = csvProvider.generateFileName();
        byte[] employeeData = csvProvider.loadEmployeeData();
        String contentType = ".csv";
        return fileStorage.saveFile(null, employeeData, fileName, contentType);
    }

    public BackupDto findLatest() {
        Backup backup = getLastBackup();
        return toDto(backup);
    }

    private Backup getLastBackup() {
        return backupRepository.findLastBackup()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Not found Backup"));
    }

    private BackupDto toDto(Backup backup) {
        return BackupDto.toDto(backup);
    }

    public CursorPageResponseBackupDto findWithSearchCondition(LocalDateTime cursor, Status status,
                                                               LocalDateTime startDate, LocalDateTime endDate,
                                                               Pageable pageable) {
        cursor = Optional.ofNullable(cursor)
                .orElse(LocalDateTime.now());
        Slice<Backup> slice = backupRepositoryImpl.findWithSearchCondition(cursor, status, startDate, endDate,
                pageable);

        List<BackupDto> content = getBackupContents(slice);

        LocalDateTime nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
        }

        Long nextIdAfter = null;
        if (slice.hasNext()) {
            nextIdAfter = content.get(content.size() - 1).id();
        }

        long count = backupRepository.count();
        return new CursorPageResponseBackupDto(content, nextCursor, nextIdAfter, content.size(), slice.hasNext(),
                count);
    }
}
