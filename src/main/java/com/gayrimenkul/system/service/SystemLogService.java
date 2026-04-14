package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.SystemLog;
import com.gayrimenkul.system.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 3. Belirli bir kullanıcının tüm hareketlerini (loglarını) getir
    @Transactional(readOnly = true)
    public List<SystemLog> getLogsByUserId(Long userId) {
        return systemLogRepository.findByUserId(userId);
    }
    
    // MÜHENDİSLİK NOTU: Log kayıtları denetim (audit) amaçlı olduğu için
    // "updateLog" ve "deleteLog" metodları BİLİNÇLİ OLARAK yazılmamıştır.
}