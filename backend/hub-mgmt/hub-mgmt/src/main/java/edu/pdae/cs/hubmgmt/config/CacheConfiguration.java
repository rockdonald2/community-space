package edu.pdae.cs.hubmgmt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration("CustomCacheConfiguration")
@EnableCaching
@RequiredArgsConstructor
public class CacheConfiguration {

    private RedisCacheConfiguration redisCacheConfigurationForLists(ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cs:hub-mgmt:" + cacheName + ":")
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }


    private RedisCacheConfiguration redisCacheConfigurationForComplexValues(ObjectMapper objectMapper) {
        var om = objectMapper = objectMapper.copy();
        om = om.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cs:hub-mgmt:" + cacheName + ":")
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(om)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .withCacheConfiguration("hub", redisCacheConfigurationForComplexValues(objectMapper))
                .withCacheConfiguration("member", redisCacheConfigurationForComplexValues(objectMapper))
                .withCacheConfiguration("hubs", redisCacheConfigurationForLists(objectMapper))
                .withCacheConfiguration("members", redisCacheConfigurationForLists(objectMapper))
                .withCacheConfiguration("waiters", redisCacheConfigurationForLists(objectMapper));
    }

}
