package com.cosangatha.bakery.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

//@Builder
@Getter
@Setter
@ToString
//Name of the hash in the Redis Server where the customer data will be stored.
@RedisHash("customer")
public class Customer {

        @Id
        private String id;

        private String name;

        private String emailAddress;

        private String address;

}
