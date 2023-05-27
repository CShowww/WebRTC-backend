package com.vd.backend.service.impl;

import com.vd.backend.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RedisCacheService implements CacheService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    @Override
    public Map<String, String> getValueByPrefix(String resource) {

        String cacheKey = getCachePrefix(resource);

        Set<String> keys = redisTemplate.keys(cacheKey + "*");

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        Map<String, String> resultMap = new HashMap<>();
        Iterator<String> keyIterator = keys.iterator();
        Iterator<String> valueIterator = values.iterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            String key = keyIterator.next();
            String value = valueIterator.next();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    @Override
    public String getCacheKey(String resource, String id) {
        return resource + ":" + id;
    }

    @Override
    public String getCachePrefix(String resource) {
        return resource + ":";
    }

}
