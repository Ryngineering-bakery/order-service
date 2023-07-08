package com.cosangatha.bakery.order.controller;

import com.cosangatha.bakery.order.model.Order;
import com.cosangatha.bakery.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value="v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping(value = "/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable("orderId") String orderId,
                                          @RequestHeader(value="correlationId") String correlationId) {
        log.info("Correlation Id : {0} " , correlationId);
        Order order = orderService.getOrder(orderId);
//        Create HATEOS links
        order.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(OrderController.class)
                .getOrder(orderId,correlationId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                        .methodOn(OrderController.class)
                        .createCustomer(null)).withRel("createOrder"));
        return ResponseEntity.ok(order);
    }

    @GetMapping(value="/search")
    public ResponseEntity<Order> getOrderByCustomerId(@RequestParam("customerId") String customerId) {
        Order order = orderService.findLatestOrderByCustomerId(customerId);
        return ResponseEntity.ok(order);
    }


    @PutMapping(value = "/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable("orderId") String orderId, @RequestBody Order toUpdate) {
        Order updatedOrder = orderService.updateOrder(orderId, toUpdate);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Order> deleteCustomer(@PathVariable("orderId") String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<Order> createCustomer(@RequestBody Order toCreate) {
        String orderId = orderService.createOrder(toCreate);
        return ResponseEntity.created(URI.create("v1/order/" + orderId)).build();
    }
}
