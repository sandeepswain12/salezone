package com.ecom.salezone.security;


import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.Role;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.Provider;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.RefreshTokenRepository;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.util.LogKeyGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Handles successful OAuth2 authentication for the SaleZone application.
 *
 * This handler is triggered when a user successfully logs in using
 * OAuth2 providers such as Google or GitHub.
 *
 * Responsibilities:
 * - Extract user details from OAuth2 provider
 * - Create a new user if the user does not exist
 * - Assign default ROLE_USER
 * - Generate JWT access token
 * - Generate and store refresh token
 * - Attach refresh token as secure HTTP-only cookie
 * - Redirect user to frontend success URL
 *
 * Supported OAuth2 Providers:
 * - Google
 * - GitHub
 *
 * Security Features:
 * - Uses JWT for stateless authentication
 * - Refresh tokens stored in database
 * - Refresh token delivered via secure cookies
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    private final RoleRepository roleRepository;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndSuccessUrl;

    /**
     * Called when OAuth2 authentication is successful.
     *
     * This method performs the following steps:
     * 1. Extract user information from OAuth2 provider
     * 2. Identify provider (Google / GitHub)
     * 3. Create new user if not already registered
     * 4. Assign default role (ROLE_USER)
     * 5. Generate JWT access token
     * 6. Generate refresh token and store it in database
     * 7. Attach refresh token in HTTP-only cookie
     * 8. Redirect user to frontend success page
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param authentication OAuth2 authentication object
     * @throws IOException if redirect fails
     * @throws ServletException if servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Successful authentication",logKey);
        log.info(authentication.toString());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //identify user:

        String registrationId = "unknown";
        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }

        log.info("LogKey: {} - registrationId: {}",logKey, registrationId);
        log.info("LogKey: {} - user: {}",logKey, oAuth2User.getAttributes().toString());

        User user;
        switch (registrationId) {
            case "google" -> {
                String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();
                String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();
                log.info("LogKey: {} - Authentication info googleId: {} email: {} name: {} picture: {}",logKey, googleId, email, name, picture);
                User newUser = User.builder()
                        .userId(UUID.randomUUID().toString())
                        .email(email)
                        .userName(name)
                        .imageName(picture)
                        .isActive(true)
                        .provider(Provider.GOOGLE)
                        .providerId(googleId)
                        .build();

                Role roleUser = roleRepository.findById("ROLE_USER")
                        .orElseThrow(() -> {
                            log.error("LogKey: {} - ROLE_USER not found", logKey);
                            return new ResourceNotFoundException("Role USER not found");
                        });

                newUser.getRoles().add(roleUser);
                log.info("LogKey: {} - User role set | role={}", logKey, roleUser);


                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
                log.info("LogKey: {} - User saved in DB {}",logKey, user);

            }

            case "github" -> {
                String name = oAuth2User.getAttributes().getOrDefault("login", "").toString();
                String githubId = oAuth2User.getAttributes().getOrDefault("id", "").toString();
                String image = oAuth2User.getAttributes().getOrDefault("avatar_url", "").toString();

                String email = (String) oAuth2User.getAttributes().get("email");
                if (email == null) {
                    email = name + "@github.com";
                }

                User newUser = User.builder()
                        .email(email)
                        .userName(name)
                        .imageName(image)
                        .isActive(true)
                        .provider(Provider.GITHUB)
                        .providerId(githubId)
                        .build();
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));

            }

            default -> {
                throw new RuntimeException("Invalid registration id");
            }

        }


        //username
        //user email
        //new usercreate


        //jwt token__ token ke sath front -- pe fir redirect.

        //refresh:
//        user--> refresh token unko revoke

//        refresh token bana ke dunga:
        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOb = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(refreshTokenOb);
        log.info("LogKey: {} - Refresh token object saved in DB {}",logKey, refreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user,logKey);
        log.info("LogKey: {} - Access token generated successfully = {} ", logKey, accessToken);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti(),logKey);
        log.info("LogKey: {} - Refresh token generated successfully = {} ", logKey, refreshToken);

        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds(),logKey);
        log.info("LogKey: {} - Refresh token attached in cookie = {} ", logKey, refreshToken);
        log.info("LogKey: {} - Login successful ", logKey);
//        response.getWriter().write("Login successful");
        response.sendRedirect(frontEndSuccessUrl);


    }
}

