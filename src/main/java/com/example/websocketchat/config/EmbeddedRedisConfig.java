package com.example.websocketchat.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Log4j2
@Profile("local") // local 환경에서 embedded redis 실행
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() {
        this.redisServer = new RedisServer(redisPort);

        try {
            redisServer.start();
        } catch (Exception e) {
            log.error("e={}", e.getMessage());
        }

    }

    @PreDestroy
    public void stopRedis() {

        if(redisServer != null) {
            redisServer.stop();
        }
    }
}
