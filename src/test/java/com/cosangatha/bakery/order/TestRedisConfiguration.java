package com.cosangatha.bakery.order;

import com.cosangatha.bakery.order.config.OrderConfig;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(OrderConfig orderConfig) {
        this.redisServer = new RedisServer(Integer.parseInt(orderConfig.getRedisPort()));
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }

}
