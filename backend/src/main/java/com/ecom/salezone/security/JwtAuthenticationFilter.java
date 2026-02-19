package com.ecom.salezone.security;

import com.ecom.salezone.repository.UserRepository;
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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * This filter runs once per request.
     * It extracts JWT token, validates it and sets authentication in SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        logger.debug("Authorization header: {}", header);

        // Check if Authorization header exists and starts with Bearer
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7); // Remove "Bearer "

            try {

                // Check if token is access token
                if (!jwtService.isAccessToken(token)) {
                    logger.warn("Invalid token type. Not an access token.");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Parse JWT
                Jws<Claims> parsedToken = jwtService.parse(token);
                Claims payload = parsedToken.getPayload();

                String userId = payload.getSubject();

                logger.debug("Token validated. UserId from token: {}", userId);

                userRepository.findById(userId).ifPresent(user -> {

                    if (user.isEnabled()) {

                        logger.info("Authenticated user: {}", user.getEmail());

                        // Convert roles to GrantedAuthority
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

                        // Set authentication in security context
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }

                    } else {
                        logger.warn("User is disabled: {}", user.getEmail());
                    }

                });

            } catch (ExpiredJwtException e) {

                logger.error("JWT token expired");
                request.setAttribute("error", "Token Expired");

            } catch (Exception e) {

                logger.error("Invalid JWT token: {}", e.getMessage());
                request.setAttribute("error", "Invalid Token");
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Skip filter for authentication endpoints.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }
}
