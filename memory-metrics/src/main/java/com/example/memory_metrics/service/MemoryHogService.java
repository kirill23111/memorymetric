package com.example.memory_metrics.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MemoryHogService {

    private final List<byte[]> chunks = new ArrayList<>();
    private final AtomicLong allocatedBytes = new AtomicLong(0);

    public MemoryHogService(MeterRegistry registry) {
        // Кастомная метрика (gauge): сколько памяти “захватили” этим сервисом
        Gauge.builder("app_memory_hog_allocated_bytes", allocatedBytes, AtomicLong::get)
                .description("Bytes allocated by MemoryHogService (heap chunks)")
                .register(registry);
    }

    /**
     * Выделяет память в куче и удерживает её, чтобы не собрал GC.
     * @param mb количество мегабайт
     */
    public synchronized long allocateMb(int mb) {
        if (mb <= 0) return allocatedBytes.get();

        int chunkSize = 1 * 1024 * 1024; // 1MB
        for (int i = 0; i < mb; i++) {
            chunks.add(new byte[chunkSize]);
            allocatedBytes.addAndGet(chunkSize);
        }
        return allocatedBytes.get();
    }

    /**
     * Освобождает всю удерживаемую память (после этого GC сможет её забрать)
     */
    public synchronized long clear() {
        chunks.clear();
        allocatedBytes.set(0);
        return 0;
    }

    public long getAllocatedBytes() {
        return allocatedBytes.get();
    }
}