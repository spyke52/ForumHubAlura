
package com.alura.forum.forumHub.infra.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleCache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public SimpleCache(long ttlMinutes) {
        this.ttlMillis = ttlMinutes * 60 * 1000;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::cleanup, 5, 5, TimeUnit.MINUTES);
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.expiryTime > System.currentTimeMillis()) {
            return entry.value;
        }
        cache.remove(key);
        return null;
    }

    public void remove(K key) {
        cache.remove(key);
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().expiryTime <= now);
    }

    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;

        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}