package com.ecom.salezone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheFlags {

    @Value("${app.cache.enabled}")
    private boolean cacheEnabled;

    @Value("${app.cache.products}")
    private boolean products;

    @Value("${app.cache.categories}")
    private boolean categories;

    @Value("${app.cache.users}")
    private boolean users;

    @Value("${app.cache.orders}")
    private boolean orders;

    @Value("${app.cache.cart}")
    private boolean cart;

    @Value("${app.cache.loadUser}")
    private boolean loadUser;

    public boolean productCacheEnabled() {
        return cacheEnabled && products;
    }

    public boolean categoryCacheEnabled() {
        return cacheEnabled && categories;
    }

    public boolean userCacheEnabled() {
        return cacheEnabled && users;
    }

    public boolean orderCacheEnabled() {
        return cacheEnabled && orders;
    }

    public boolean cartCacheEnabled() {
        return cacheEnabled && cart;
    }

    public boolean loadUserCacheEnabled() {
        return cacheEnabled && loadUser;
    }
}