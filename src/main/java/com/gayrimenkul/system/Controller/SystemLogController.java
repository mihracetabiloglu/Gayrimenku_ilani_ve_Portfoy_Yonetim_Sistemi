package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.entity.SystemLog;
import com.gayrimenkul.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    public ResponseEntity<List<SystemLog>> getAllLogs() {
        return ResponseEntity.ok(systemLogService.getAllLogs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SystemLog>> getLogsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(systemLogService.getLogsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<SystemLog> saveLog(@RequestBody SystemLog systemLog) {
        SystemLog savedLog = systemLogService.saveLog(systemLog);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }
}
