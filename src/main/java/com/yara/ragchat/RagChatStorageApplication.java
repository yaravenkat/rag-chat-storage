
package com.yara.ragchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableCaching                // enable @Cacheable, @CacheEvict
@EnableRedisHttpSession
public class RagChatStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(RagChatStorageApplication.class, args);
    }
}
