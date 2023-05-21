package com.vd.backend.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheImpl extends LinkedHashMap<String, String> {

    int capacity = 50;

    public CacheImpl(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    public String get(int key) {
        return super.getOrDefault(key, " ");
    }

    public String put(String key, String value) {
        super.put(key, value);
        return key;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return size() > capacity;
    }
}
