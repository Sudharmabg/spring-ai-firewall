package com.sudharma.firewall.redis;

import com.sudharma.firewall.core.RateLimiter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class RedisRateLimiter implements RateLimiter {
    
    private final StringRedisTemplate redisTemplate;
    
    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public boolean isAllowed(String key, int requestsPerMinute) {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - 60_000; // 1 minute window
        
        // Remove old entries outside window
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        
        // Count current requests in window
        Long count = redisTemplate.opsForZSet().count(key, windowStart, now);
        
        if (count != null && count >= requestsPerMinute) {
            return false;
        }
        
        // Add current request
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
        
        return true;
    }
    
    @Override
    public long getRequestCount(String key) {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - 60_000;
        
        Long count = redisTemplate.opsForZSet().count(key, windowStart, now);
        
        return count != null ? count : 0;
    }
}
