package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.SystemLog;
import com.gayrimenkul.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/logs", "/api/admin/logs"})
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemLog>> getAllLogs(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "all") String range,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(systemLogService.searchLogs(q, range, status, sortDir));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemLog>> getLogsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(systemLogService.getLogsByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemLog> saveLog(@RequestBody SystemLog systemLog) {
        SystemLog savedLog = systemLogService.saveLog(systemLog);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }
}
