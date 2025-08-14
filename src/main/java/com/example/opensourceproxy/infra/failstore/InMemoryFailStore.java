package com.example.opensourceproxy.infra.failstore;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFailStore implements FailStore {

    private final ConcurrentHashMap<String, FailStoreEntry> store = new ConcurrentHashMap<>();

    @Override
    public void save(FailStoreEntry entry) {
        store.put(entry.getId(), entry);
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }

    @Override
    public List<FailStoreEntry> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void bumpRetry(String id, String lastError) {
        store.computeIfPresent(id, (k, v) -> {
            v.setRetryCount(v.getRetryCount() + 1);
            v.setLastError(lastError);
            return v;
        });
    }
}