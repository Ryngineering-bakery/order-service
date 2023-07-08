package com.cosangatha.bakery.order.util;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static final UserContext getContext() {
        UserContext context = userContext.get();

        if (context == null) {
            context = new UserContext();
            userContext.set(context);
        }
        return context;
    }


    public  static final void setUserContext(UserContext context) {
        userContext.set(context);
    }
}
