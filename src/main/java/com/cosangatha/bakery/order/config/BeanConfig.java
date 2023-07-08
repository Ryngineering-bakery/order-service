package com.cosangatha.bakery.order.config;

import com.cosangatha.bakery.order.client.CustomerClient;
import com.cosangatha.bakery.order.client.CustomerRestTemplateClient;
import com.cosangatha.bakery.order.repository.CustomerRedisRepository;
import com.cosangatha.bakery.order.util.UserContext;
import com.cosangatha.bakery.order.util.UserContextHolder;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BeanConfig {


    private final OrderConfig orderConfig;



    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
//                .filter((clientRequest, nextFilter) -> {
//            log.info("Webclient CorrelationId Filter : {} " ,UserContextHolder.getContext().getCorrelationId());
//            log.info("Request Headers : {} " , clientRequest.headers());
//            ClientRequest clientRequestWithCorrId = ClientRequest
//                    .from(clientRequest)
//                    .header(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId())
//                    .build();
////            clientRequest.headers().add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
//            return nextFilter.exchange(clientRequestWithCorrId);
//        });
    }


    @Bean
    public WebClient webClient(@Autowired WebClient.Builder loadBalancedWebclient) {
        return loadBalancedWebclient.build();
    }

    @Bean(name = "customerClient")
//    TODO : REACTIVE ENABLED - Enable the below option.
    @ConditionalOnProperty(name = "feign.enabled", havingValue = "false")
    public CustomerClient CustomerRestTemplateClient(@Autowired WebClient webClient, @Autowired CustomerRedisRepository redisRepository ,@Autowired ObservationRegistry observationRegistry) {
        return new CustomerRestTemplateClient(webClient,redisRepository,observationRegistry);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        String hostname = orderConfig.getRedisServer();
        int port = Integer.parseInt(orderConfig.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Autowired JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

}
