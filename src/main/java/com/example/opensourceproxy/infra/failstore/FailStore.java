package com.example.opensourceproxy.infra.failstore;

import java.util.List;

public interface FailStore {
    void save(FailStoreEntry entry);
    void remove(String id);
    List<FailStoreEntry> findAll();
    void bumpRetry(String id, String lastError);
}