package ecommerce.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private static final String CACHE_PREFIX = "cache:";
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;
    private final HashOperations<String, Object, Object> hashOps;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.hashOps = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    //    ========================CACHE=====================

    //put
    public void putCache(String key, Object value) {
        putCache(key, value, 0, null);
    }

    public void putCache(String key, Object value, long ttl, TimeUnit ttlUnit) {
        if (value == null) return;
        try {
            String realKey = CACHE_PREFIX + key;
            if (ttl > 0 && ttlUnit != null) {
                valueOps.set(realKey, value, ttl, ttlUnit);
            } else {
                valueOps.set(realKey, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi serialize object sang JSON", e);
        }
    }

    //get
    public Object getCache(String key) {
        return valueOps.get(CACHE_PREFIX + key);
    }

    public boolean existsCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CACHE_PREFIX + key));
    }

    //delete
    public void delCache(String key) {
        redisTemplate.delete(CACHE_PREFIX + key);
    }

    //====================TOKEN==================

    // Lưu token vào blacklist
    public void blacklistCache(String token, long ttl, TimeUnit ttlUnit) {
        if (token == null) return;
        String realKey = BLACKLIST_PREFIX + token;
        valueOps.set(realKey, true, ttl, ttlUnit);
    }

    // Kiểm tra token có trong blacklist không
    public boolean isTokenBlacklisted(String token) {
        if (token == null) return false;
        Boolean exists = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
        return Boolean.TRUE.equals(exists);
    }

    //xóa token
    public void removeTokenBlacklisted(String token) {
        if (token == null) return;
        redisTemplate.delete(BLACKLIST_PREFIX + token);
    }
}

