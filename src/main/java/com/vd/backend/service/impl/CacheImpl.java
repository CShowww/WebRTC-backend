package com.vd.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


@Component
@Scope("singleton")
public class CacheImpl extends LinkedHashMap<String, String> {

    @Value("${common.cache_capacity}")
    int capacity;

    public CacheImpl(@Value("${common.cache_capacity}") int capacity) {
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
