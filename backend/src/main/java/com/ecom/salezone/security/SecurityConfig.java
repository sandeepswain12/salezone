package com.ecom.salezone.security;

import com.ecom.salezone.dtos.ApiError;
import com.ecom.salezone.util.LogKeyGenerator;
import com.ecom.salezone.util.SalezoneConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final Logger log =
            LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("SecurityConfig - Initializing security filter chain");

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(SalezoneConstants.AUTH_PUBLIC_URLS).permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth ->
                        oauth.successHandler(oAuth2SuccessHandler)
                                .failureHandler(null)
                )
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {

                    // 🔥 Generate logKey here
                    String logKey = LogKeyGenerator.generateLogKey();

                    log.error("LogKey: {} - Unauthorized access attempt | path={} reason={}",
                            logKey, request.getRequestURI(), e.getMessage());

                    response.setStatus(401);
                    response.setContentType("application/json");

                    String message = e.getMessage();
                    String error = (String) request.getAttribute("error");

                    if (error != null) {
                        message = error;
                    }

                    ApiError apiError = new ApiError();
                    apiError.setStatus(HttpStatus.UNAUTHORIZED.value());
                    apiError.setMessage(message);
                    apiError.setTimestamp(OffsetDateTime.now());
                    apiError.setPath(request.getRequestURI());
                    apiError.setError("Unauthorized Access");

                    var objectMapper = new ObjectMapper();
                    response.getWriter().write(objectMapper.writeValueAsString(apiError));
                }))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        log.info("SecurityConfig - Security filter chain configured successfully");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {

        log.info("SecurityConfig - AuthenticationManager bean initialized");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        log.info("SecurityConfig - BCryptPasswordEncoder bean initialized");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.front-end-url}") String corsUrls) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Initializing CORS configuration | allowedOrigins={}",
                logKey, corsUrls);

        String[] urls = corsUrls.trim().split(",");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(urls));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        log.info("LogKey: {} - CORS configuration completed successfully", logKey);

        return source;
    }
}