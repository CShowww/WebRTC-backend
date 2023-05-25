package com.vd.backend.service;

public interface CacheService {

    void set(String key, String value);

    String get(String key);

    Boolean del(String key);

    Boolean hasKey(String key);

}
