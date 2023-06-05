package edu.pdae.cs.activitynotificationsmgmt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Optional;

@Configuration("CustomCacheConfiguration")
@EnableCaching
@RequiredArgsConstructor
public class CacheConfiguration {

    private RedisCacheConfiguration redisCacheConfigurationForLists(ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cs:activity-notifications-mgmt:" + cacheName + ":")
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }

    private RedisCacheConfiguration redisCacheConfigurationForComplexValues(ObjectMapper objectMapper, Optional<Integer> ttlInMinutes) {
        var om = objectMapper = objectMapper.copy();
        om = om.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        final int ttl = ttlInMinutes.orElse(5);

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cs:activity-notifications-mgmt:" + cacheName + ":")
                .entryTtl(Duration.ofMinutes(ttl))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(om)));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .withCacheConfiguration("activities", redisCacheConfigurationForComplexValues(objectMapper, Optional.empty()))
                .withCacheConfiguration("activities-grouped", redisCacheConfigurationForLists(objectMapper))
                .withCacheConfiguration("hub", redisCacheConfigurationForComplexValues(objectMapper, Optional.empty()))
                .withCacheConfiguration("member", redisCacheConfigurationForComplexValues(objectMapper, Optional.empty()))
                .withCacheConfiguration("memo", redisCacheConfigurationForComplexValues(objectMapper, Optional.empty()))
                .withCacheConfiguration("notifications", redisCacheConfigurationForLists(objectMapper));
    }

    @Bean
    public <V> RedisTemplate<String, V> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public LockProvider lockProvider(LettuceConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }

}