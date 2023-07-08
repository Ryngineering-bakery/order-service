package com.cosangatha.bakery.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

//@Builder
@Getter
@Setter
@ToString
@Entity
//@NoArgsConstructor
@Table(name = "Orders")
public class Order extends RepresentationModel<Order> {

//        TODO : automated UUID generator
        @Id
        @Column(name="order_id")
        private String id;

        private String productId;

        private String customerId;

        private Float totalAmount;

}
