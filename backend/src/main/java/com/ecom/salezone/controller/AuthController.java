package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.enities.RefreshToken;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.RefreshTokenRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.security.CookieService;
import com.ecom.salezone.security.JwtService;
import com.ecom.salezone.services.AuthService;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.services.OtpService;
import com.ecom.salezone.services.impl.PasswordResetService;
import com.ecom.salezone.util.LogKeyGenerator;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * AuthController handles authentication related APIs for the SaleZone E-commerce system.
 *
 * This controller provides endpoints for:
 * - User Registration (Signup)
 * - User Login with JWT Authentication
 * - Refreshing Access Tokens using Refresh Tokens
 * - User Logout and Refresh Token Revocation
 *
 * Authentication Flow:
 * 1. User logs in with email and password.
 * 2. System generates:
 *      - Short-lived Access Token (JWT)
 *      - Long-lived Refresh Token
 * 3. Refresh Token is stored in HttpOnly Secure Cookie.
 * 4. Access Token is used for API authentication.
 * 5. When Access Token expires, client calls /refresh to get a new token.
 * 6. Logout revokes refresh token and clears cookies.
 *
 * Security Features:
 * - JWT based authentication
 * - Refresh token rotation
 * - Refresh token stored in database
 * - Token revocation support
 * - HttpOnly secure cookie for refresh token
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "Authentication APIs",
        description = "APIs for user authentication including signup, login, refresh token and logout"
)
@RestController
@RequestMapping("/salezone/ecom/auth")
public class AuthController {

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

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Operation(
            summary = "Register a new user",
            description = "Creates account and sends OTP to email for verification."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered, OTP sent to email"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(
            @Valid @RequestBody SignupRequestDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Signup request received | email={}", logKey, userDto.getEmail());

        UserDto createdUser = authService.registerUser(userDto, logKey);
        log.info("LogKey: {} - Signup completed, OTP sent | userId={}", logKey, createdUser.getUserId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Registration successful. Please check your email for the OTP.",
                        "email", userDto.getEmail()
                ));
    }

    @Operation(summary = "User Login - Step 1",
            description = "Verifies credentials and sends OTP. Returns a pre-auth token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "OTP sent to email"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account not verified")
    })
    @PostMapping("/login")
    public ResponseEntity<PreAuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Login attempt | email={}", logKey, loginRequest.getEmail());

        // verify credentials
        authenticate(loginRequest, logKey);

        // load user
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found after auth | email={}", logKey, loginRequest.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        // check account is active (email verified)
        if (!user.isEnabled()) {
            log.warn("LogKey: {} - Login blocked | account not active | email={}", logKey, user.getEmail());
            throw new DisabledException("Account not verified. Please verify your email first.");
        }

        // generate pre-auth token (cryptographic proof password was verified)
        String preAuthToken = jwtService.generatePreAuthToken(user, logKey);
        log.info("LogKey: {} - Pre-auth token generated | email={}", logKey, user.getEmail());

        // generate and send OTP
        String otp = otpService.generateOtp(user.getEmail(), OtpType.LOGIN, logKey);
        emailService.sendOtpEmail(user.getEmail(), user.getUserName(), otp, OtpType.LOGIN, logKey);

        log.info("LogKey: {} - Login OTP sent | email={}", logKey, user.getEmail());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new PreAuthResponse(preAuthToken, "OTP sent to your email.", user.getEmail()));
    }

    @Operation(summary = "Verify OTP",
            description = "For LOGIN: verifies pre-auth token + OTP, issues JWT tokens. " +
                    "For REGISTRATION: verifies OTP, activates account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "401", description = "Invalid pre-auth token")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request,
            HttpServletResponse response) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - OTP verify request | email={} type={}", logKey, request.getEmail(), request.getType());

        // REGISTRATION
        if (request.getType() == OtpType.REGISTRATION) {

            otpService.verifyOtp(request.getEmail(), request.getCode(), OtpType.REGISTRATION, logKey);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Activate account and mark email as verified
            user.setActive(true);
            user.setEmailVerified(true);
            userRepository.save(user);

            log.info("LogKey: {} - Account activated + email verified | email={}", logKey, request.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "Account verified successfully. You can now login."
            ));
        }

        // LOGIN
        if (request.getType() == OtpType.LOGIN) {

            // preAuthToken must be present
            if (request.getPreAuthToken() == null || request.getPreAuthToken().isBlank()) {
                log.warn("LogKey: {} - Pre-auth token missing | email={}", logKey, request.getEmail());
                throw new BadCredentialsException("Pre-auth token is required for login OTP verification");
            }

            // Validate signature + expiry + typ=preauth
            if (!jwtService.isPreAuthToken(request.getPreAuthToken())) {
                log.warn("LogKey: {} - Invalid pre-auth token | email={}", logKey, request.getEmail());
                throw new BadCredentialsException("Invalid or expired pre-auth token");
            }

            // Extract email from signed token — never trust request body alone
            String emailFromToken = jwtService.getEmail(request.getPreAuthToken());

            // Binding check — email in token must match request
            if (!emailFromToken.equals(request.getEmail())) {
                log.warn("LogKey: {} - Pre-auth token email mismatch | tokenEmail={} requestEmail={}",
                        logKey, emailFromToken, request.getEmail());
                throw new BadCredentialsException("Token does not match the provided email");
            }

            // Verify OTP
            otpService.verifyOtp(request.getEmail(), request.getCode(), OtpType.LOGIN, logKey);

            // Load user
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Create and save refresh token
            String jti = UUID.randomUUID().toString();
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .jti(jti)
                    .user(user)
                    .createdAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                    .revoked(false)
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);
            log.info("LogKey: {} - Refresh token saved | jti={}", logKey, jti);

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user, logKey);
            String refreshToken = jwtService.generateRefreshToken(user, jti, logKey);

            // Attach cookie
            cookieService.attachRefreshCookie(response, refreshToken,
                    (int) jwtService.getRefreshTtlSeconds(), logKey);
            cookieService.addNoStoreHeaders(response, logKey);

            emailService.sendWelcomeEmail(user.getEmail(), user.getUserName(), logKey);

            // Build response
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(accessToken);
            tokenResponse.setRefreshToken(refreshToken);
            tokenResponse.setExpiresIn(jwtService.getAccessTtlSeconds());
            tokenResponse.setUser(modelMapper.map(user, UserDto.class));

            log.info("LogKey: {} - Login complete, tokens issued | email={}", logKey, user.getEmail());

            return ResponseEntity.ok(tokenResponse);
        }

        throw new BadCredentialsException("Invalid OTP type");
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Generates a new access token using a valid refresh token stored in cookie or request."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "403", description = "Refresh token expired or revoked")
    })
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
        cookieService.addNoStoreHeaders(response,logKey);
        log.info("LogKey: {} - New Refresh token attached to cookie = {}",logKey,jti);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(newAccessToken);
        tokenResponse.setRefreshToken(newRefreshToken);
        tokenResponse.setExpiresIn(jwtService.getAccessTtlSeconds());
        tokenResponse.setUser(modelMapper.map(user, UserDto.class));

        log.info("LogKey: {} - Refresh successful | userId={}", logKey, user.getUserId());

        return ResponseEntity.ok(tokenResponse);
    }


    @Operation(
            summary = "Logout User",
            description = "Logs out the user by revoking refresh token and clearing authentication cookies."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
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

        cookieService.clearRefreshCookie(response,logKey);
        cookieService.addNoStoreHeaders(response,logKey);
        log.info("LogKey: {} - Cleared refresh token from cookie ", logKey);

        SecurityContextHolder.clearContext();
        log.info("LogKey: {} - Cleared context holder", logKey);

        log.info("LogKey: {} - Logout completed", logKey);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //  OTP FLOW

    @Operation(summary = "Request password reset OTP",
            description = "Sends a 6-digit OTP to the logged-in user's email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/password-reset/otp/request")
    public ResponseEntity<Map<String, String>> requestOtpReset(
            @Valid @RequestBody PasswordResetRequestDto request) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - OTP reset request received", logKey);

        passwordResetService.requestOtpReset(request.getEmail(), logKey);

        return ResponseEntity.ok(Map.of(
                "message", "If this email is registered, an OTP has been sent."
        ));
    }


    @Operation(summary = "Verify OTP and reset password",
            description = "Verifies the OTP and sets the new password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    @PostMapping("/password-reset/otp/verify")
    public ResponseEntity<Map<String, String>> verifyOtpAndReset(
            @Valid @RequestBody PasswordResetOtpRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - OTP reset verify received", logKey);

        passwordResetService.verifyOtpAndReset(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword(),
                logKey
        );

        return ResponseEntity.ok(Map.of("message", "Password reset successful. Please login again."));
    }

    //  LINK FLOW 

    @Operation(summary = "Request password reset link",
            description = "Sends a signed reset link to the logged-in user's email. Valid for 15 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset link sent"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/password-reset/link/request")
    public ResponseEntity<Map<String, String>> requestLinkReset(
            @Valid @RequestBody PasswordResetRequestDto request) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Link reset request received", logKey);

        passwordResetService.requestLinkReset(request.getEmail(), logKey);

        return ResponseEntity.ok(Map.of(
                "message", "If this email is registered, a reset link has been sent."
        ));
    }



    @Operation(summary = "Verify reset link token and reset password",
            description = "Validates the signed reset token and sets the new password. " +
                    "This endpoint is PUBLIC — no access token needed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/password-reset/link/verify")
    public ResponseEntity<Map<String, String>> verifyLinkAndReset(
            @Valid @RequestBody PasswordResetLinkVerifyRequest request) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("LogKey: {} - Link reset verify received", logKey);

        passwordResetService.verifyLinkAndReset(
                request.getResetToken(),
                request.getNewPassword(),
                logKey
        );

        return ResponseEntity.ok(Map.of("message", "Password reset successful. Please login again."));
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
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (LockedException e) {
            // isActive=false — email not verified yet
            log.warn("LogKey: {} - Login blocked | account locked | email={}", logKey, loginRequest.getEmail());
            throw new DisabledException("Account not verified. Please check your email for the OTP.");
        } catch (Exception e) {
            log.error("LogKey: {} - Authentication failed | email={}", logKey, loginRequest.getEmail());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * Extracts the authenticated user from SecurityContext.
     * JwtAuthenticationFilter stores email as the principal.
     */
    private User getAuthenticatedUser(String logKey) {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("LogKey: {} - Authenticated user email from context | email={}", logKey, email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
