package com.example.memory_metrics.controller;

import com.example.memory_metrics.service.MemoryHogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/memory")
public class MemoryController {

    private final MemoryHogService memoryHogService;

    public MemoryController(MemoryHogService memoryHogService) {
        this.memoryHogService = memoryHogService;
    }

    // Пример: POST /memory/allocate?mb=200
    @PostMapping("/allocate")
    public ResponseEntity<Map<String, Object>> allocate(@RequestParam(defaultValue = "100") int mb) {
        long allocated = memoryHogService.allocateMb(mb);
        return ResponseEntity.ok(Map.of(
                "allocatedMb", allocated / (1024 * 1024),
                "allocatedBytes", allocated
        ));
    }

    // Пример: POST /memory/clear
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clear() {
        memoryHogService.clear();
        return ResponseEntity.ok(Map.of("allocatedMb", 0, "allocatedBytes", 0));
    }

    // Пример: GET /memory/status
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        long allocated = memoryHogService.getAllocatedBytes();
        return ResponseEntity.ok(Map.of(
                "allocatedMb", allocated / (1024 * 1024),
                "allocatedBytes", allocated
        ));
    }
}