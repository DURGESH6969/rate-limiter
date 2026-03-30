package com.ratelimiter.rate_limiter.algorithm;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class SlidingWindowCounter {

    private final RedisTemplate<String, String> redisTemplate;

    public SlidingWindowCounter(RedisTemplate<String,String>redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String clientKey, String endpoint,int limit, int windowSeconds) {

        // Get current time in seconds (e.g. 1711234567)
        long currentBucket = System.currentTimeMillis() / 1000;

        // Build a unique Redis key for this client + endpoint + second
        // Example: "rate:192.168.1.1:/api/search:1711234567"
        String redisKey = buildKey(clientKey, endpoint, currentBucket);

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, windowSeconds + 1, TimeUnit.SECONDS);
        }

        long total = countRequestsInWindow(clientKey, endpoint, currentBucket, windowSeconds);

        return total <= limit;
    }

    private long countRequestsInWindow(String clientKey, String endpoint, long currentBucket, int windowSeconds) {
        long total = 0;

        for (long i = 0; i < windowSeconds; i++) {
            String key = buildKey(clientKey, endpoint, currentBucket - i);
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                total += Long.parseLong(value);
            }
        }
        return total;
    }

    public long getRemainingRequests(String clientKey, String endpoint, int limit, int windowSeconds) {
        long currentBucket = System.currentTimeMillis() / 1000;
        long total = countRequestsInWindow(clientKey, endpoint, currentBucket, windowSeconds);
        return Math.max(0, limit - total);
    }


        private String buildKey(String clientKey, String endpoint, long bucket) {
            return String.format("rate:%s:%s:%d", clientKey, endpoint, bucket);
    }
}
