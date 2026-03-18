package com.ecom.salezone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * Redis configuration for the SaleZone application.
 *
 * This configuration enables Redis-based caching for the application.
 * Cached data is stored in Redis with a configurable TTL (Time-To-Live).
 *
 * Features:
 * - Configurable cache expiration
 * - JSON serialization for cache values
 * - Null value caching disabled
 *
 * Example usage:
 *
 * @Cacheable("products")
 * public ProductDto getProduct(String id)
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 2026
 */
@Configuration
public class RedisConfig {

    @Value("${app.cache.ttl.minutes}")
    private long cacheTtlMinutes;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(cacheTtlMinutes))
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}