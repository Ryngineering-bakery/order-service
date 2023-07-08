package com.cosangatha.bakery.order.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserContext {
    public static final String CORRELATION_ID = "x-correlation-id";
    public static final String AUTH_TOKEN = "x-auth-id";
    public static final String USER_ID = "x-user-id";

    private String correlationId;

    private String authToken;

    private String userId;


}
