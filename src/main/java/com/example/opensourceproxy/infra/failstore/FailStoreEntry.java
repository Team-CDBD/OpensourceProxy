package com.example.opensourceproxy.infra.failstore;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FailStoreEntry {
    private String id;
    private String topic;
    private String key;
    private String message;
    private int retryCount;
    private String lastError;
    private Instant createdAt;

    public static FailStoreEntry of(String topic, String key, String message, String lastError) {
        return new FailStoreEntry(UUID.randomUUID().toString(), topic, key, message, 0, lastError, Instant.now());
    }
}