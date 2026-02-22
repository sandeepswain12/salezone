package com.ecom.salezone.security;

import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.util.LogKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

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