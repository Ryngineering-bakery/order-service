package com.cosangatha.bakery.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestRedisConfiguration.class)
// TODO : Test tries connecting to kafka broker.. See options to disable it ( KAFKA READING )
class OrderApplicationTests {

	@Test
	void contextLoads() {
	}



}
