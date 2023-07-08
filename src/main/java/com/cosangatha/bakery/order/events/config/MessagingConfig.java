package com.cosangatha.bakery.order.events.config;

import com.cosangatha.bakery.order.events.handlers.CustomerChangeHandler;
import com.cosangatha.bakery.order.events.model.CustomerChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class MessagingConfig {

    @Bean
//    Bean name configured within config file for spring's awareness.
    public Consumer<CustomerChangeModel> cacheEvictionSink(@Autowired CustomerChangeHandler customerChangeHandler) {
        return customerChangeHandler.cacheEviction();
    }

}
