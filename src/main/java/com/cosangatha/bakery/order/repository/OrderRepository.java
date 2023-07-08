package com.cosangatha.bakery.order.repository;

import com.cosangatha.bakery.order.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order,String> {
    public List<Order> findByCustomerId(String customerId);

    public List<Order> findByProductId(String productId);
}
