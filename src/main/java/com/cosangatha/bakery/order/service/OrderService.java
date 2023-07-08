package com.cosangatha.bakery.order.service;

import com.cosangatha.bakery.order.client.CustomerClient;
import com.cosangatha.bakery.order.client.CustomerRestTemplateClient;
import com.cosangatha.bakery.order.config.OrderConfig;
import com.cosangatha.bakery.order.model.Customer;
import com.cosangatha.bakery.order.model.Order;

import com.cosangatha.bakery.order.repository.OrderRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderConfig config;

    private final CustomerRestTemplateClient customerClient;

//    Supplier<Order> newDummy = () -> Order.builder().id("1").name("Test").emailAddress("Test@Gmail.com").address("XYZ Street").build();


    @CircuitBreaker(name="orderService")
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No Order Found"));

    }

    public String createOrder(Order order) {
        log.info("Config Source : {0} , Creating a order" , config.getConfigSource() );
        order.setId(UUID.randomUUID().toString());
        orderRepository.save(order);
        return order.getId();
    }

    public Order updateOrder(String orderId, Order toUpdate) {
        log.info("[ ] Updating a order for orderId ");
        Order modifiedOrder = orderRepository.findById(orderId)
                .map(order ->   {
                    order.setProductId(toUpdate.getProductId());
                    order.setCustomerId(toUpdate.getCustomerId());
                    order.setTotalAmount(toUpdate.getTotalAmount());
                    return order;
                }).orElseThrow(() -> new IllegalArgumentException("No Order Found"));
        Order orderFromDB = orderRepository.save(modifiedOrder);
        return orderFromDB;
    }


    public void deleteOrder(String orderId){
        Order toDelete = new Order();
         toDelete.setId(orderId);
        orderRepository.delete(toDelete);
        log.info("[ ] Deleting a order");
    }

//    Resiliency pattern - circuit breaker.
    @CircuitBreaker(name="CustomerService" , fallbackMethod = "buildFallbackOrder")
    @Retry(name="retryCustomerService",fallbackMethod = "buildFallbackOrder")
    // BulkHead Threadpool is onlyu applicable to methods that return CompletableFutures.
//    @Bulkhead(name="bulkCustomerService", type= Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackOrder")
    public Order findLatestOrderByCustomerId(String customerId){
//        Customer customer = customerClient.getCustomerDetails().apply(customerId);
        Customer customer = customerClient.getCustomerDetailsMono(customerId).block();
        if( customer != null) {
            return findOrderByCustomerId().apply(customerId);
        }
        throw new IllegalArgumentException("Invalid Customer Id");
    }


    private Order buildFallbackOrder(String customerId, Throwable t) {
        log.error(ExceptionUtils.getFullStackTrace(t));
        return new Supplier<Order>() {
            @Override
            public Order get() {
                Order order = new Order();
                order.setTotalAmount(0.00f);
                order.setProductId("DummyId");
                order.setCustomerId("DummyCustomerId");
                order.setId("DummyId");
                return order;
            }
        }.get();
    }

    private Function<String, Order> findOrderByCustomerId() {
        return (customerId) -> orderRepository.findByCustomerId(customerId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Order Found"));
    }



}
