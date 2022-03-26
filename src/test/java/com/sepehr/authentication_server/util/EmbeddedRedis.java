package com.sepehr.authentication_server.util;

import org.springframework.boot.test.context.TestConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class EmbeddedRedis {

    private final redis.embedded.RedisServer redisServer;

    public EmbeddedRedis() throws IOException {
        this.redisServer = new redis.embedded.RedisServer(6375);
    }

    @PostConstruct
    public void startRedis() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }
}
