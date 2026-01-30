package com.ecom.salezone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req

                        // USERS - ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/salezone/ecom/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/salezone/ecom/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/salezone/ecom/users/**").hasRole("ADMIN")

                        // PRODUCTS - ADMIN ONLY (write)
                        .requestMatchers(HttpMethod.POST, "/salezone/ecom/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/salezone/ecom/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/products/**").hasRole("ADMIN")

//                        // Orders - ADMIN ONLY (write)
//                        .requestMatchers(HttpMethod.POST, "/salezone/ecom/orders/**").hasRole("USER")
//                        .requestMatchers(HttpMethod.PUT, "/salezone/ecom/orders/**").hasRole("USER")
//                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/orders/**").hasRole("USER")
//                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/orders/**").hasRole("USER")

//                        // PUBLIC READ APIs
//                        .requestMatchers(HttpMethod.GET, "/salezone/ecom/products/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/salezone/ecom/categories/**").permitAll()

                        // EVERYTHING ELSE
                        .anyRequest().permitAll()
                );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
