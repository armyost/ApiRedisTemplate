package com.example.demo;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.var;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

        @Bean
        RedisTemplate<String, SampleEntity> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
                final var template = new RedisTemplate<String, SampleEntity>();

                template.setConnectionFactory(lettuceConnectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new Jackson2JsonRedisSerializer<>(SampleEntity.class));

                return template;
        }
}