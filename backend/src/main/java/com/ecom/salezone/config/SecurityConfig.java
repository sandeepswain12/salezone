package com.ecom.salezone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> req
//                                .requestMatchers("/salezone/ecom/auth/**").permitAll()
//                                .requestMatchers("/salezone/ecom/categories/**").permitAll()
//                                .requestMatchers("/salezone/ecom/products/**").permitAll()

//                        // USERS - ADMIN ONLY
//                        .requestMatchers(HttpMethod.POST, "/salezone/ecom/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/salezone/ecom/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/salezone/ecom/users/**").hasRole("ADMIN")
//
//                        // PRODUCTS - ADMIN ONLY (write)
//                        .requestMatchers(HttpMethod.POST, "/salezone/ecom/products/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/salezone/ecom/products/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/salezone/ecom/products/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.GET, "/salezone/ecom/products/**").hasRole("USER")

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
