package com.saleset;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Set<String>> eventCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.HOURS) // Cache expires after 3 hours
                .maximumSize(5000) // Store up to 5,000 entries
                .build();
    }

}
