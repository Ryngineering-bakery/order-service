package com.cosangatha.bakery.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import reactor.core.publisher.Hooks;

@SpringBootApplication
// exposes refresh endpoint to refresh custom configurations.
@RefreshScope
@EnableDiscoveryClient
//@EnableFeignClients
@Slf4j


public class OrderApplication {

	public static void main(String[] args) {

		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(OrderApplication.class, args);
	}


//	RestTemplate load balancer did not work.
//	@LoadBalanced
//	@Bean
//	public RestTemplate restTemplate(){
//		RestTemplate restTemplate = new RestTemplate();
//		log.info("{0}" , restTemplate);
//		return restTemplate;
//	}

}
