package com.ecom.salezone.security;


import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.Provider;
import com.ecom.salezone.repository.RefreshTokenRepository;
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

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

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
        response.getWriter().write("Login successful");
//        response.sendRedirect(frontEndSuccessUrl);


    }
}

