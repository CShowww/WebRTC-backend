package com.vd.backend.service.impl;

import com.vd.backend.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;

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
    public List<String> getValueByPrefix(String keyPrefix) {

        Set<String> keys = redisTemplate.keys(keyPrefix + "*");

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        return values;
    }
}
