package com.ratelimiter.rate_limiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration // Tells Spring: this class contains configuration/setup code

public class RedisConfig {

    @Bean // Tells Spring: manage this object, make it available everywhere

    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();

        // Connect it to Redis
        template.setConnectionFactory(factory);



        // Store keys as plain text (not binary)
        // So in Redis you'll see: "rate:192.168.1.1:/api/search"
        // Instead of: "\xac\xed\x00\x05t\x00\x04test"
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());


        template.afterPropertiesSet();

        return template;

    }

}


