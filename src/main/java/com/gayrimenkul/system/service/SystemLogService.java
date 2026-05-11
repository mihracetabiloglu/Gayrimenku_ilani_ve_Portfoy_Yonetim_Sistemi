package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.SystemLog;
import com.gayrimenkul.system.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    // 1. Yeni Log Oluştur (Sistemde bir olay olduğunda çağrılır)
    @Transactional
    public SystemLog createLog(Long userId, String action, String details, String ipAddress) {
        
        SystemLog log = SystemLog.builder()
                .userId(userId)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .createdBy(userId) // İşlemi yapan kişinin ID'sini "createdBy" olarak atıyoruz
                .build();

        return systemLogRepository.save(log);
    }

    // Alternatif Log Oluşturucu (Direkt nesne alarak)
    @Transactional
    public SystemLog saveLog(SystemLog systemLog) {
        return systemLogRepository.save(systemLog);
    }

    // 2. Tüm Logları Listele (Adminlerin sistemi izlemesi için)
    @Transactional(readOnly = true)
    public List<SystemLog> getAllLogs() {
        // İleride buraya sayfalamalı (Pagination) getirme eklenebilir
        return systemLogRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SystemLog> searchLogs(String query, String range, String status, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        LocalDateTime startDate = resolveStartDate(range);
        String normalizedQuery = normalize(query);
        String normalizedStatus = normalize(status);

        return systemLogRepository.findAll(Sort.by(direction, "createdAt"))
                .stream()
                .filter(log -> startDate == null || (log.getCreatedAt() != null && !log.getCreatedAt().isBefore(startDate)))
                .filter(log -> normalizedStatus.isBlank() || "all".equals(normalizedStatus) || normalize(resolveStatus(log)).contains(normalizedStatus))
                .filter(log -> normalizedQuery.isBlank() || searchableText(log).contains(normalizedQuery))
                .toList();
    }

    // 3. Belirli bir kullanıcının tüm hareketlerini (loglarını) getir
    @Transactional(readOnly = true)
    public List<SystemLog> getLogsByUserId(Long userId) {
        return systemLogRepository.findByUserId(userId);
    }

    private LocalDateTime resolveStartDate(String range) {
        String normalized = normalize(range);
        if ("last-hour".equals(normalized) || "son-saat".equals(normalized)) {
            return LocalDateTime.now().minusHours(1);
        }
        if ("last-24-hours".equals(normalized) || "son-24-saat".equals(normalized)) {
            return LocalDateTime.now().minusHours(24);
        }
        if ("last-week".equals(normalized) || "son-hafta".equals(normalized)) {
            return LocalDateTime.now().minusWeeks(1);
        }
        return null;
    }

    private String searchableText(SystemLog log) {
        return normalize(String.join(" ",
                String.valueOf(log.getId()),
                String.valueOf(log.getUserId()),
                String.valueOf(log.getCreatedBy()),
                String.valueOf(log.getAction()),
                String.valueOf(log.getDetails()),
                String.valueOf(log.getIpAddress()),
                String.valueOf(log.getCreatedAt()),
                resolveStatus(log)));
    }

    private String resolveStatus(SystemLog log) {
        String action = normalize(log.getAction());
        if (action.contains("unauthorized") || action.contains("error") || action.contains("fail")) {
            return "BASARISIZ";
        }
        if (action.contains("login") || action.contains("success")) {
            return "BASARILI";
        }
        return "NULL";
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.forLanguageTag("tr-TR")).trim();
    }
    
    // MÜHENDİSLİK NOTU: Log kayıtları denetim (audit) amaçlı olduğu için
    // "updateLog" ve "deleteLog" metodları BİLİNÇLİ OLARAK yazılmamıştır.
}
