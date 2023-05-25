package com.vd.backend.service;

import java.util.List;

public interface CacheService {

    void set(String key, String value);

    String get(String key);

    Boolean del(String key);

    Boolean hasKey(String key);

    List<String> getValueByPrefix(String keyPrefix);

}
