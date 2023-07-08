package com.cosangatha.bakery.order.client;

import com.cosangatha.bakery.order.model.Customer;
import com.cosangatha.bakery.order.repository.CustomerRedisRepository;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerRestTemplateClient implements CustomerClient{

    private final WebClient loadbalancedWebClient;

    private final CustomerRedisRepository redisRepository;

    private final ObservationRegistry observationRegistry;

    @Autowired
    private Tracer tracer;


    private Customer checkRedisCache(String customerId) {
        //        Create a custom span
//        Create a span .. If a span is present in the Thread it will be the parent of "cacheCustomerSpan" span
        Span cacheCustomerSpan = this.tracer.nextSpan().name("readCustomerDataFromRedis");
        //        Start a span and put it in scope.. Putting in scope means the span is in thread local and added to MDC to contain tracing information.
        this.tracer.withSpan(cacheCustomerSpan.start());
        try {
            log.info("Retrieve Cached customer with id : {} ", customerId);
            return redisRepository.findById(customerId).orElse(null);
        } catch(Exception ex) {
            log.error("Error when retrieving customer information from Redis cache : {} ", ex);
            return null;
        } finally {
//            tag a  span for debugging
            cacheCustomerSpan.tag("peer.service","redis");
//            log an event on a span
            cacheCustomerSpan.event("Customer Cache Populated");
//            end span
            cacheCustomerSpan.end();
        }
    }

    private void cacheCustomerObject(Customer customer) {
        try {

            redisRepository.save(customer);
        } catch(Exception ex) {
            log.error("Error when Storing customer information from Redis cache : {} ", ex);
        }
    }


    public Mono<Customer> getCustomerDetailsMono(String customerId) {
        Observation observation = Observation.start("webclient-customer", observationRegistry);
        return Mono.just(observation).flatMap(span -> {
                    observation.scoped(() -> log.info("<ACCEPTANCE_TEST> <TRACE:{}> Hello from consumer",
                            this.tracer.currentSpan().context().traceId()));
                    return this.loadbalancedWebClient.get()
                            .uri("http://CUSTOMER-SERVICE/v1/customer/" + customerId)
                            .retrieve()
                            .bodyToMono(Customer.class)
                            .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation));
                })
                .doFinally(signalType -> observation.stop())
                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation));
//        return Mono.deferContextual(contextView -> {
//            ContextSnapshot.setThreadLocalsFrom(contextView, ObservationThreadLocalAccessor.KEY);
//            String traceId = tracer.currentSpan().context().traceId();
//            log.info("<ACCEPTANCE_TEST> <TRACE:{}> Hello!", traceId);
//            return this.loadbalancedWebClient.get().uri("http://CUSTOMER-SERVICE/v1/customer/" + customerId)
//                    .retrieve()
//                    .bodyToMono(Customer.class);
//            });
    }

    @Override
    public Function<String, Customer> getCustomerDetails() {
//        TODO : REACTIVE - understand webclient
//        TODO : Remove Hardcoded userContext .
        return customerId ->
//                Check customer in cache or else get customer details by invoking customer service.
            Optional.ofNullable(checkRedisCache(customerId))
                    .orElseGet(() -> {
                        log.info("Customer Details not found in Cache , customer id : {} . Retrieving customer information buy invoking customer service" , customerId);
//                        Observation observation = Observation.start("webclient-customer", observationRegistry);
//                        Mono<Customer> customerMono = Mono.just(observation).flatMap(span -> {
//                                observation.scoped(() -> log.info("Trace : {} ", this.tracer.currentSpan().context().traceId()));
//                                    return loadbalancedWebClient
//                                            .get()
//                                            .uri("http://CUSTOMER-SERVICE/v1/customer/" + customerId)
//                                            .retrieve()
//                                            .bodyToMono(Customer.class)
//                                            .doOnNext(customer -> {
//                                                if (customer != null) {
//                                                    log.info("Caching customer with customer Id : {} ", customer.getId());
//                                                    cacheCustomerObject(customer);
//                                                }
//                                            });
//                                })
//                                .doFinally(signalType -> observation.stop())
//                                .contextCapture();
//                                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation));
                        return loadbalancedWebClient
                                .get()
                                .uri("http://CUSTOMER-SERVICE/v1/customer/" + customerId)
                                .header("traceId" , this.tracer.currentSpan().context().traceId())
                                .retrieve()
                                .bodyToMono(Customer.class)
                                .doOnNext(customer -> {
                                    if (customer != null) {
                                        log.info("Caching customer with customer Id : {} ", customer.getId());
                                        log.info("Trace ID from doNext : {} ", this.tracer.currentSpan().context().traceId());
                                        cacheCustomerObject(customer);
                                    }
                                })
                                .contextCapture()
                                .block();
//                        return customerMono.block();
                    });
    }


}
