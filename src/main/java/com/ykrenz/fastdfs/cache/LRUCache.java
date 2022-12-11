package com.ykrenz.fastdfs.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> implements FdfsCache<K, V> {
    private final Map<K, V> cache;

    public LRUCache(int maxSize) {
        cache = new LinkedHashMap<K, V>(maxSize, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
