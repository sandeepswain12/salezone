package com.ecom.salezone.security;

import com.ecom.salezone.dtos.ApiError;
import com.ecom.salezone.util.SalezoneConstants;
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

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    /**
     * Main Spring Security configuration.
     * - Enables CORS
     * - Disables CSRF (since using JWT)
     * - Configures authorization rules
     * - Adds JWT filter before UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS
                .cors(Customizer.withDefaults())

                // Disable CSRF because we are using stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Authorization rules
                .authorizeHttpRequests(req -> req
                                .requestMatchers(SalezoneConstants.AUTH_PUBLIC_URLS).permitAll()
//                      .requestMatchers(SalezoneConstants.AUTH_ADMIN_URLS).hasRole(SalezoneConstants.ADMIN_ROLE)
//                      .requestMatchers(SalezoneConstants.AUTH_USER_URLS).hasRole(SalezoneConstants.USER_ROLE)
                                .anyRequest().authenticated()
                ).oauth2Login(oauth ->
                        oauth.successHandler(oAuth2SuccessHandler)
                                .failureHandler(null)
                )
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
                    //error message
                    e.printStackTrace();
                    response.setStatus(401);
                    response.setContentType("application/json");
                    String message = e.getMessage();
                    String error = (String) request.getAttribute("error");
                    if (error != null) {
                        message = error;
                    }
//                    Map<String, Object> errorMap = Map.of("message", message, "statusCode",404);
                    ApiError apiError = new ApiError();
                    apiError.setStatus(HttpStatus.UNAUTHORIZED.value());
                    apiError.setMessage(message);
                    apiError.setTimestamp(OffsetDateTime.now());
                    apiError.setPath(request.getRequestURI());
                    apiError.setError("Unauthorized Access");
                    var objectMapper = new ObjectMapper();
                    response.getWriter().write(objectMapper.writeValueAsString(apiError));
                }))

                // Add JWT filter before Spring's default authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager bean required for login authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Password encoder using BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS configuration.
     * Reads allowed frontend URLs from application properties.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.front-end-url}") String corsUrls) {

        String[] urls = corsUrls.trim().split(",");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(urls));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
