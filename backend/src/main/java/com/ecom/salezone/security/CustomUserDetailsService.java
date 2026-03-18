package com.ecom.salezone.security;

import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.util.LogKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 *
 * This service is responsible for loading user details from the database
 * during authentication.
 *
 * It retrieves user information using the email (username) and returns
 * a {@link UserDetails} object required by Spring Security to perform
 * authentication and authorization.
 *
 * Features:
 * - Fetches user data from the database using UserRepository
 * - Supports caching for improved performance
 * - Logs authentication attempts with a unique logKey
 *
 * This service is used by Spring Security during the login process.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Cacheable(
            value = "users",
            key = "#username",
            condition = "@cacheFlags.loadUserCacheEnabled()"
    )
    /**
     * Loads user details by username (email).
     *
     * This method is called by Spring Security during the authentication process.
     * It retrieves the user from the database and returns the corresponding
     * {@link UserDetails} object.
     *
     * Results may be cached to improve authentication performance.
     *
     * @param username the email of the user attempting to authenticate
     * @return UserDetails object containing user credentials and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - loadUserByUsername called | username={}",
                logKey, username);

        UserDetails userDetails = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | username={}",
                            logKey, username);
                    return new UsernameNotFoundException(
                            "User not found with email: " + username);
                });

        log.info("LogKey: {} - User loaded successfully | username={}",
                logKey, username);

        return userDetails;
    }
}