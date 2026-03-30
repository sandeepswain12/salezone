package com.ecom.salezone.security;

import com.ecom.salezone.enities.User;
import com.ecom.salezone.enities.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for handling JSON Web Token (JWT) operations
 * for the SaleZone E-commerce application.
 *
 * This service provides functionality for:
 * - Generating JWT Access Tokens
 * - Generating JWT Refresh Tokens
 * - Parsing and validating JWT tokens
 * - Extracting claims such as userId, email, roles, and token id
 *
 * Tokens are signed using HS512 algorithm and configured using
 * application properties.
 *
 * Access Token:
 *  - Short-lived
 *  - Contains userId, email, roles
 *
 * Refresh Token:
 *  - Long-lived
 *  - Used to generate new access tokens
 *
 * Security:
 *  - Uses secret key with minimum 512-bit length
 *  - Tokens include issuer, expiration, and unique identifier (JTI)
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Service
@Getter
@Setter
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey secretKey;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    /**
     * Initializes JWT configuration from application properties.
     *
     * @param secretKey secret key used for signing JWT tokens
     * @param accessTtlSeconds access token expiration time in seconds
     * @param refreshTtlSeconds refresh token expiration time in seconds
     * @param issuer token issuer identifier
     */
    public JwtService(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer}") String issuer
    ) {

        // Validate secret key length (minimum 512-bit for HS512)
        if (secretKey == null || secretKey.length() < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 characters long");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;

        log.info("JWT Service initialized with issuer: {}", issuer);
    }

    /**
     * Generates a JWT Access Token for the given user.
     *
     * The access token contains:
     * - userId as subject
     * - email
     * - roles
     * - token type (access)
     *
     * @param user authenticated user entity
     * @param logKey unique request identifier for logging
     * @return signed JWT access token
     */
    public String generateAccessToken(User user,String logKey) {

        log.info("LogKey: {} - Entry into generateAccessToken with user = {} }", logKey, user);
        Instant now = Instant.now();

        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList();

        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUserId())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        log.debug("Access token generated for userId: {}", user.getUserId());

        return token;
    }

    /**
     * Generates a JWT Refresh Token.
     *
     * Refresh tokens are long-lived tokens used to obtain
     * new access tokens without requiring re-authentication.
     *
     * @param user authenticated user entity
     * @param jti unique token identifier
     * @param logKey unique request identifier for logging
     * @return signed JWT refresh token
     */
    public String generateRefreshToken(User user, String jti,String logKey) {

        log.info("LogKey: {} - Entry into generateRefreshToken with  jti = {} and user = {}}", logKey,jti, user);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .id(jti)
                .subject(user.getUserId())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        log.debug("Refresh token generated for userId: {}", user.getUserId());

        return token;
    }

    /**
     * Generates a short-lived pre-auth token after password verification.
     * typ=preauth — cannot be used as an access token.
     * TTL is 5 minutes — enough time to check email and enter OTP.
     */
    public String generatePreAuthToken(User user, String logKey) {

        log.info("LogKey: {} - Generating pre-auth token | userId={}", logKey, user.getUserId());

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUserId())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(300)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "typ", "preauth"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a short-lived password reset token.
     * typ=pwdreset — 15 minutes TTL.
     * Cannot be used as access, refresh, or preauth token.
     */
    public String generatePwdResetToken(User user, String logKey) {

        log.info("LogKey: {} - Generating pwd-reset token | userId={}", logKey, user.getUserId());

        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUserId())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900))) // 15 minutes
                .claims(Map.of(
                        "email", user.getEmail(),
                        "typ", "pwdreset"
                ))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Returns true only if token carries typ=pwdreset.
     */
    public boolean isPwdResetToken(String token) {
        Claims claims = parse(token).getPayload();
        return "pwdreset".equals(claims.get("typ"));
    }

    /**
     * Returns true only if token carries typ=preauth.
     */
    public boolean isPreAuthToken(String token) {
        Claims claims = parse(token).getPayload();
        return "preauth".equals(claims.get("typ"));
    }

    /**
     * Parses and validates a signed JWT token.
     *
     * @param token JWT token string
     * @return parsed JWT claims
     */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Checks if token is Access Token.
     */
    public boolean isAccessToken(String token) {
        Claims claims = parse(token).getPayload();
        return "access".equals(claims.get("typ"));
    }

    /**
     * Checks if token is Refresh Token.
     */
    public boolean isRefreshToken(String token) {
        Claims claims = parse(token).getPayload();
        return "refresh".equals(claims.get("typ"));
    }

    /**
     * Extracts userId (subject) from JWT token.
     *
     * @param token JWT token
     * @return userId stored in token
     */
    public String getUserId(String token) {
        return parse(token).getPayload().getSubject();
    }

    /**
     * Extract JWT ID (jti).
     */
    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    /**
     * Extract roles from token.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return (List<String>) parse(token).getPayload().get("roles");
    }

    /**
     * Extract email from token.
     */
    public String getEmail(String token) {
        return (String) parse(token).getPayload().get("email");
    }
}
