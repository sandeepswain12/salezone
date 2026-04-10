package com.ecom.salezone.security;

import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.util.LogKeyGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter for the SaleZone application.
 *
 * This filter intercepts every incoming HTTP request and performs
 * JWT-based authentication.
 *
 * Responsibilities:
 * - Extract JWT token from the Authorization header
 * - Validate the JWT token using JwtService
 * - Check token type (must be access token)
 * - Extract user details from token
 * - Load user from database
 * - Set authentication in Spring SecurityContext
 *
 * Security Flow:
 * 1. Client sends request with Authorization header (Bearer Token)
 * 2. Filter extracts the token
 * 3. Token is validated and parsed
 * 4. User is authenticated if token is valid
 * 5. SecurityContext is populated with authenticated user
 *
 * This filter runs once per request using OncePerRequestFilter.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Core filter method executed once for every request.
     *
     * This method performs JWT authentication by:
     * - Reading Authorization header
     * - Extracting Bearer token
     * - Validating token using JwtService
     * - Loading user details from database
     * - Creating Authentication object
     * - Storing authentication in SecurityContext
     *
     * If token is expired or invalid, appropriate error attributes
     * are set on the request for later handling.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain for continuing request processing
     * @throws ServletException if servlet processing fails
     * @throws IOException if IO error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Generate logKey for this request
        String logKey = LogKeyGenerator.generateLogKey();

        String header = request.getHeader("Authorization");

        logger.debug("LogKey: {} - JWT Filter invoked | AuthorizationHeader={}",
                logKey, header);

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {

                if (!jwtService.isAccessToken(token)) {
                    logger.warn("LogKey: {} - Invalid token type (Not access token)", logKey);
                    filterChain.doFilter(request, response);
                    return;
                }

                Jws<Claims> parsedToken = jwtService.parse(token);
                Claims payload = parsedToken.getPayload();

                String userId = payload.getSubject();

                logger.debug("LogKey: {} - Token validated | userId={}", logKey, userId);

                userRepository.findById(userId).ifPresent(user -> {

                    if (user.isEnabled()) {

                        logger.info("LogKey: {} - User authenticated | email={}",
                                logKey, user.getEmail());

                        List<GrantedAuthority> authorities =
                                user.getRoles() == null
                                        ? List.of()
                                        : user.getRoles()
                                        .stream()
                                        .map(role ->
                                                new SimpleGrantedAuthority(role.getRoleName()))
                                        .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        null,
                                        authorities
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext()
                                    .setAuthentication(authentication);
                        }

                    } else {
                        logger.warn("LogKey: {} - User account disabled | email={}",
                                logKey, user.getEmail());
                    }

                });

            } catch (ExpiredJwtException e) {

                logger.error("LogKey: {} - JWT token expired", logKey);
                request.setAttribute("error", "Token Expired");

            } catch (Exception e) {

                logger.error("LogKey: {} - Invalid JWT token | reason={}",
                        logKey, e.getMessage());
                request.setAttribute("error", "Invalid Token");
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determines whether this filter should be skipped for the request.
     *
     * Authentication endpoints are excluded because they
     * do not require JWT validation.
     *
     * @param request HTTP request
     * @return true if filter should be skipped
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }
}