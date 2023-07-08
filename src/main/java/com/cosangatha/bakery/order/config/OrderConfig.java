package com.cosangatha.bakery.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

// NOTE: @Configuration is required to register the class as a bean.
@Configuration
//@ConfigurationProperties(prefix = "example")
@Getter
@Setter
public class OrderConfig {

    private String configSource;

    @Value("${redis.server}")
    private String redisServer = "";

    @Value("${redis.port}")
    private String redisPort="";
}
