package com.ecom.salezone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cache feature flags configuration.
 *
 * This class controls enabling or disabling cache
 * at both global and module level.
 *
 * Example:
 *
 * app.cache.enabled=true
 * app.cache.products=true
 * app.cache.categories=false
 *
 * If global cache is disabled, all module caches
 * will automatically be disabled.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Component
@ConfigurationProperties(prefix = "app.cache")
public class CacheFlags {

    private boolean enabled;
    private boolean products;
    private boolean categories;
    private boolean users;
    private boolean orders;
    private boolean cart;
    private boolean loadUser;

    public boolean productCacheEnabled() {
        return enabled && products;
    }

    public boolean categoryCacheEnabled() {
        return enabled && categories;
    }

    public boolean userCacheEnabled() {
        return enabled && users;
    }

    public boolean orderCacheEnabled() {
        return enabled && orders;
    }

    public boolean cartCacheEnabled() {
        return enabled && cart;
    }

    public boolean loadUserCacheEnabled() {
        return enabled && loadUser;
    }

    // getters and setters
}