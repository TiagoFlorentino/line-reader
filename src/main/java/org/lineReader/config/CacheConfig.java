package org.lineReader.config;

import com.github.benmanes.caffeine.cache.*;
import org.springframework.cache.*;
import org.springframework.cache.caffeine.*;
import org.springframework.context.annotation.*;

import java.util.concurrent.*;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES) // Expire after 10 minutes (could be more or less depending on requirements)
                .maximumSize(100_000); // 100 000 lines max size
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("linesCache");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
