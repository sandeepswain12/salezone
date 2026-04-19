package com.ecom.salezone.util;

public class SalezoneConstants {
    public static final String[] AUTH_PUBLIC_URLS = {
            "/salezone/ecom/auth/**",

            //  REMOVE THIS
            // "/salezone/ecom/auth/**"

            "/salezone/ecom/categories/**",
            "/salezone/ecom/products/**",
            "/salezone/ecom/wishlist/**",
            "/salezone/ecom/users/**",
            "/salezone/ecom/address/**",
            "/salezone/ecom/admin/**",
            "/salezone/ecom/carts/**",
            "/salezone/ecom/orders/**",

            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public static final String[] AUTH_ADMIN_URLS= {
            "/salezone/ecom/users/"
    };

    public static final String[] AUTH_USER_URLS= {

    };

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";

//    other project-related constants
}
