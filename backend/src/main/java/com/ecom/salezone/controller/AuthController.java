package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.repository.RefreshTokenRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.security.CookieService;
import com.ecom.salezone.security.JwtService;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.util.LogKeyGenerator;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/salezone/ecom/auth")
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CookieService cookieService;

    /**
     * Handles user signup request.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupRequestDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Signup request received | email={}", logKey, userDto.getEmail());

        UserDto createdUser = authService.registerUser(userDto, logKey);

        log.info("LogKey: {} - Signup completed | userId={}", logKey, createdUser.getUserId());

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Handles login request.
     * Authenticates user, generates access & refresh tokens,
     * and stores refresh token in database.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Login attempt | credential = {}", logKey, loginRequest);

        // Authenticate credentials
        Authentication authenticate = authenticate(loginRequest, logKey);
        log.info("LogKey: {} - Login Successful | credential = {}", logKey, authenticate);

        // Fetch user from DB
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Login failed | user not found", logKey);
                    return new BadCredentialsException("Invalid email or password");
                });

        // Check user status
        if (!user.isEnabled()) {
            log.warn("LogKey: {} - Login blocked | user disabled", logKey);
            throw new DisabledException("User is disabled");
        }

        // Create refresh token entry (DB persistence)
        String jti = UUID.randomUUID().toString();
        log.info("LogKey: {} - Refresh token id generated = {} ", logKey, jti);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        log.info("LogKey: {} - Refresh token data saved successfully in DB = {} ", logKey, refreshTokenEntity);

        log.debug("LogKey: {} - Refresh token persisted | jti={}", logKey, jti);

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user,logKey);
        log.info("LogKey: {} - Access token generated successfully = {} ", logKey, accessToken);
        String refreshToken = jwtService.generateRefreshToken(user, jti,logKey);
        log.info("LogKey: {} - Refresh token generated successfully = {} ", logKey, refreshToken);

        // use cookie service to attach refresh token in cookie
        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds(),logKey);
        cookieService.addNoStoreHeaders(response);
        log.info("LogKey: {} - Refresh token attached in cookie = {} ", logKey, refreshToken);

        // Build response
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiresIn(jwtService.getAccessTtlSeconds());
        tokenResponse.setUser(modelMapper.map(user, UserDto.class));
        log.info("LogKey: {} - Login successful | payload = {}", logKey, tokenResponse);

        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Rotates refresh token and issues new access + refresh tokens.
     * Validates token from cookie/body/header before rotation.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Refresh token request received", logKey);

        // Extract refresh token
        String refreshToken = readRefreshTokenFromRequest(body, request,logKey)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Refresh token missing", logKey);
                    return new BadCredentialsException("Refresh token is missing");
                });
        log.info("LogKey: {} - Got refresh token = {}",logKey,refreshToken);

        // Validate token type
        if (!jwtService.isRefreshToken(refreshToken)) {
            log.warn("LogKey: {} - Invalid refresh token type", logKey);
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtService.getJti(refreshToken);
        String userId = jwtService.getUserId(refreshToken);
        log.debug("LogKey: {} - Refresh token parsed | jti={} userId={}", logKey, jti, userId);

        // Fetch stored token from DB
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - Refresh token not recognized | jti={}", logKey, jti);
                    return new BadCredentialsException("Refresh token not recognized");
                });
        log.info("LogKey: {} - Fetched stored refresh token from DB = {}",logKey,storedRefreshToken);

        // Validate stored token
        if (storedRefreshToken.isRevoked()) {
            log.warn("LogKey: {} - Refresh token revoked | jti={}", logKey, jti);
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("LogKey: {} - Refresh token expired | jti={}", logKey, jti);
            throw new BadCredentialsException("Refresh token expired");
        }

        if (!storedRefreshToken.getUser().getUserId().equals(userId)) {
            log.error("LogKey: {} - Refresh token user mismatch | jti={}", logKey, jti);
            throw new BadCredentialsException("Refresh token does not belong to this user");
        }

        // Rotate refresh token
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        log.debug("LogKey: {} - Old refresh token revoked | oldJti={} newJti={}",
                logKey, jti, newJti);

        User user = storedRefreshToken.getUser();

        RefreshToken newRefreshTokenOb = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenOb);
        log.info("LogKey: {} - New refresh token saved in DB = {}",logKey,newRefreshTokenOb);

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user,logKey);
        log.info("LogKey: {} - New access token generated = {}",logKey,newAccessToken);
        String newRefreshToken = jwtService.generateRefreshToken(user, newJti,logKey);
        log.info("LogKey: {} - New refresh token generated = {}",logKey,newRefreshToken);

        cookieService.attachRefreshCookie(response, newRefreshToken,
                (int) jwtService.getRefreshTtlSeconds(),logKey);
        cookieService.addNoStoreHeaders(response);
        log.info("LogKey: {} - New Refresh token attached to cookie = {}",logKey,jti);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(newAccessToken);
        tokenResponse.setRefreshToken(newRefreshToken);
        tokenResponse.setExpiresIn(jwtService.getAccessTtlSeconds());
        tokenResponse.setUser(modelMapper.map(user, UserDto.class));

        log.info("LogKey: {} - Refresh successful | userId={}", logKey, user.getUserId());

        return ResponseEntity.ok(tokenResponse);
    }


    /**
     * Logs out user.
     * Revokes refresh token (if present) and clears authentication context.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Logout request received", logKey);

        readRefreshTokenFromRequest(null, request , logKey).ifPresent(token -> {
            try {
                if (jwtService.isRefreshToken(token)) {
                    log.info("LogKey: {} - Validating refresh token | token = {}", logKey, token);
                    String jti = jwtService.getJti(token);

                    refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                        log.debug("LogKey: {} - Refresh token revoked during logout | jti={}",
                                logKey, jti);
                    });
                }
            } catch (JwtException e) {
                log.warn("LogKey: {} - Invalid refresh token during logout", logKey);
            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        log.info("LogKey: {} - Cleared refresh token from cookie ", logKey);

        SecurityContextHolder.clearContext();
        log.info("LogKey: {} - Cleared context holder", logKey);

        log.info("LogKey: {} - Logout completed", logKey);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Reads refresh token from:
     * 1. Cookie (preferred)
     * 2. Request body
     * 3. Custom header (X-Refresh-Token)
     * 4. Authorization header (Bearer)
     */
    private Optional<String> readRefreshTokenFromRequest(
            RefreshTokenRequest body,
            HttpServletRequest request,
            String logKey) {

        log.info("LogKey: {} - Entry into readRefreshTokenFromRequest with body = {} request = {}", logKey,body,request);

        // Prefer cookie
        if (request.getCookies() != null) {
            log.info("LogKey: {} - Getting token from cookie = {}",logKey,request.getCookies());
            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }

        // Request body
        if (body != null && body.getRefreshToken() != null
                && !body.getRefreshToken().isBlank()) {
            log.info("LogKey: {} - Getting token from body = {}",logKey,body);
            return Optional.of(body.getRefreshToken());
        }

        // Custom header
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            log.info("LogKey: {} - Getting token from header = {}",logKey,refreshHeader);
            return Optional.of(refreshHeader.trim());
        }

        // Authorization header fallback
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            log.info("LogKey: {} - Getting token from auth header = {}",logKey,authHeader);
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }


    /**
     * Authenticates user credentials using Spring Security.
     */
    private Authentication authenticate(LoginRequest loginRequest, String logKey) {

        try {
            log.info("LogKey: {} - Entry into authenticate with login credential = {}", logKey,loginRequest);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("LogKey: {} - Authentication failed | email={}", logKey, loginRequest.getEmail());
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }
}
