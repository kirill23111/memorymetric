package com.example.memory_metrics.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final MeterRegistry registry;

    public DemoController(MeterRegistry registry) {
        this.registry = registry;
    }

    private void inc(String endpoint, int status) {
        Counter.builder("app_demo_requests_total")
                .description("Total requests to demo endpoints")
                .tag("endpoint", endpoint)
                .tag("status", String.valueOf(status))
                .register(registry)
                .increment();
    }

    // 200
    @GetMapping("/ok")
    public ResponseEntity<Map<String, Object>> ok() {
        inc("/demo/ok", 200);
        return ResponseEntity.ok(Map.of("message", "OK"));
    }

    // 404
    @GetMapping("/not-found")
    public ResponseEntity<Map<String, Object>> notFound() {
        inc("/demo/not-found", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Not Found"));
    }

    // 500
    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> error() {
        inc("/demo/error", 500);
        throw new RuntimeException("Simulated 500 error");
    }

    // Метод “с кастомной метрикой” (по сути счётчик тоже увеличим)
    @PostMapping("/custom-metric")
    public ResponseEntity<Map<String, Object>> customMetric() {
        inc("/demo/custom-metric", 200);
        return ResponseEntity.ok(Map.of("message", "Custom metric incremented"));
    }
}