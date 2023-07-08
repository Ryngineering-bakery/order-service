package com.cosangatha.bakery.order.repository;

import com.cosangatha.bakery.order.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRedisRepository extends CrudRepository<Customer, String> {
}
