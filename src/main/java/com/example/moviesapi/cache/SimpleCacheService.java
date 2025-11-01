package com.example.moviesapi.cache;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class SimpleCacheService {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(10); // 10 minutes

    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }

    public void put(String key, Object value, long ttlMillis) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMillis));
    }

    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }
        cache.remove(key); // Remove expired entry
        return null;
    }

    public boolean contains(String key) {
        CacheEntry entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return (int) cache.values().stream()
                .filter(entry -> !entry.isExpired())
                .count();
    }

    public Map<String, Object> getStats() {
        long currentTime = System.currentTimeMillis();
        int totalEntries = cache.size();
        int activeEntries = (int) cache.values().stream()
                .filter(entry -> !entry.isExpired())
                .count();
        int expiredEntries = totalEntries - activeEntries;

        return Map.of(
            "totalEntries", totalEntries,
            "activeEntries", activeEntries,
            "expiredEntries", expiredEntries,
            "cacheSize", size()
        );
    }

    private static class CacheEntry {
        private final Object value;
        private final long expirationTime;

        public CacheEntry(Object value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}