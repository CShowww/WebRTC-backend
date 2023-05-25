package com.vd.backend.service;

import java.util.List;
import java.util.Map;

public interface CacheService {

    void set(String key, String value);

    String get(String key);

    Boolean del(String key);

    Boolean hasKey(String key);

    Map<String, String> getValueByPrefix(String keyPrefix);

}
