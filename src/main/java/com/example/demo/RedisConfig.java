package com.example.demo;

import java.time.Duration;
import java.util.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.var;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {
        
        // @Value("${spring.redis.host}")
        // private String host;

        // @Value("${spring.redis.port}")
        // private int port;

        // @Bean(name = "redisCacheConnectionFactory")
        // public RedisConnectionFactory redisCacheConnectionFactory() {
        //         return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
        // }

        @Bean
        RedisTemplate<String, SampleEntity> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
                final var template = new RedisTemplate<String, SampleEntity>();

                template.setConnectionFactory(lettuceConnectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new Jackson2JsonRedisSerializer<>(SampleEntity.class));

                return template;
        }
}

// {

// @Value("${spring.redis.host}")
// private String host;

// @Value("${spring.redis.port}")
// private int port;

// @Bean(name = "redisCacheConnectionFactory")
// public RedisConnectionFactory redisConnectionFactory() {
// return new LettuceConnectionFactory(host, port);
// }

// @Bean
// public CacheManager contentCacheManager() {
// RedisCacheManager.RedisCacheManagerBuilder builder =
// RedisCacheManager.RedisCacheManagerBuilder
// .fromConnectionFactory(redisConnectionFactory());
// RedisCacheConfiguration configuration =
// RedisCacheConfiguration.defaultCacheConfig()
// .serializeValuesWith(RedisSerializationContext.SerializationPair
// .fromSerializer(new GenericJackson2JsonRedisSerializer())) // Value
// Serializer 변경
// .prefixKeysWith("Sample:") // Key Prefix로 "Sample:"를 앞에 붙여 저장
// .entryTtl(Duration.ofMinutes(30)); // 캐시 수명 30분
// builder.cacheDefaults(configuration);
// return builder.build();
// }

// @Bean
// public RedisTemplate<String, Object> redisTemplate(
// @Qualifier("redisCacheConnectionFactory") RedisConnectionFactory
// redisConnectionFactory
// ){
// ObjectMapper mapper = getObjectMapper();
// GenericJackson2JsonRedisSerializer redisSerializer = new
// GenericJackson2JsonRedisSerializer(mapper);

// RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
// redisTemplate.setConnectionFactory(redisConnectionFactory);
// redisTemplate.setKeySerializer(new StringRedisSerializer());
// redisTemplate.setValueSerializer(redisSerializer);

// return redisTemplate;
// }

// private static ObjectMapper getObjectMapper() {
// ObjectMapper mapper = new ObjectMapper();
// mapper.activateDefaultTyping(
// BasicPolymorphicTypeValidator
// .builder()
// .allowIfSubType(Object.class) //모든 객체의 타입정보를 저장할 수 있도록 설정
// .build(),
// ObjectMapper.DefaultTyping.NON_FINAL
// );
// mapper.registerModule(new JavaTimeModule()); //LocaldateTime 저장을 위해 등록
// mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
// //LocaldateTime을 Day까지 반환해줌
// return mapper;
// }
// }