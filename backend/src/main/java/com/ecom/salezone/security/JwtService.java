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
     * Constructor initializes JWT configuration from application properties.
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
     * Generates JWT Access Token.
     * Includes userId, email and roles.
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
     * Generates Refresh Token.
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
     * Parses and validates JWT token.
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
     * Extract userId from token.
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
