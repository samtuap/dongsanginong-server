package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTool {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setExpire(String key, Object value, Long duration) {

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Duration expireDuratoin = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuratoin);
    }


    public <T> T getValue(String key, Class<T> clazz) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object value = valueOperations.get(key);
        if (value != null) {
            return clazz.cast(value);
        }
        return null;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}