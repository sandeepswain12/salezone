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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * This filter runs once per request.
     * It extracts JWT token, validates it and sets authentication in SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔥 Generate logKey for this request
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
     * Skip filter for authentication endpoints.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }
}