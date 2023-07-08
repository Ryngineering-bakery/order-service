package com.cosangatha.bakery.order.events.handlers;

import com.cosangatha.bakery.order.events.model.CustomerChangeModel;
import com.cosangatha.bakery.order.repository.CustomerRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerChangeHandler {

    private final CustomerRedisRepository redisRepository;

    public Consumer<CustomerChangeModel> cacheEviction() {
        return model -> {
            log.info("Received an {} event for customer id : {} . Evicting Redis cache entry ",
                    model.getAction(),
                    model.getCustomerId());
//            EVICT REDIS CACHE
            try {
                redisRepository.deleteById(model.getCustomerId());
            } catch (Exception ex) {
                log.error("Error when deleting entry from cache for customer id : {}\n ex: {} " , model.getCustomerId() , ex);
            }
        };
    }

}
