package com.cosangatha.bakery.order.client;

import com.cosangatha.bakery.order.model.Customer;

import java.util.function.Function;

public interface CustomerClient {

    Function<String, Customer> getCustomerDetails();

}
