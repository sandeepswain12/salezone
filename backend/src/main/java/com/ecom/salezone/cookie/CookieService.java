package com.ecom.salezone.cookie;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing HTTP cookies used for authentication.
 *
 * This service handles operations related to refresh token cookies such as:
 * - Attaching refresh token cookies to HTTP responses
 * - Clearing refresh token cookies during logout
 * - Setting secure cookie attributes (HttpOnly, Secure, SameSite, Domain)
 * - Adding cache control headers for security
 *
 * Cookies are configured using application properties to allow flexible
 * security configuration for different environments.
 *
 * Security Features:
 * - HttpOnly cookies prevent JavaScript access
 * - Secure flag ensures cookies are sent only over HTTPS
 * - SameSite attribute helps protect against CSRF attacks
 *
 * This service is mainly used during authentication and logout flows.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
@Service
@Getter
public class CookieService {

    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String cookieSameSite;

    private  final Logger logger = org.slf4j.LoggerFactory.getLogger(CookieService.class);

    public CookieService(

            @Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
            @Value("${security.jwt.cookie-same-site}") String cookieSameSite,
            @Value("${security.jwt.cookie-domain}") String cookieDomain
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
    }

    /**
     * Attaches refresh token cookie to the HTTP response.
     *
     * The cookie is configured with security attributes such as
     * HttpOnly, Secure, SameSite and Domain.
     *
     * @param response HTTP response object
     * @param value refresh token value
     * @param maxAge cookie expiration time in seconds
     * @param logKey unique request identifier used for logging
     */
    public void attachRefreshCookie(HttpServletResponse response, String value, int maxAge, String logKey) {

        logger.info("LogKey: {} - Attaching cookie with name: {} and value: {}",logKey, refreshTokenCookieName, value);
        var responseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookieSameSite);

        if(cookieDomain!=null && !cookieDomain.isBlank())
        {
            responseCookieBuilder.domain(cookieDomain);

        }
        ResponseCookie responseCookie = responseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    /**
     * Clears the refresh token cookie from the client.
     *
     * This is typically used during logout operations.
     * The cookie is removed by setting its maxAge to zero.
     *
     * @param response HTTP response object
     * @param logKey unique request identifier used for logging
     */
    public void clearRefreshCookie(HttpServletResponse response,String logKey) {
        logger.warn("LogKey: {} - Clearing refresh cookie | name={}",
                logKey, refreshTokenCookieName);
        var builder = ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(cookieHttpOnly)
                .path("/")
                .sameSite(cookieSameSite)
                .secure(cookieSecure);

        if(cookieDomain!=null && !cookieDomain.isBlank())
        {
            builder.domain(cookieDomain);

        }

        ResponseCookie responseCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    }

    /**
     * Adds cache control headers to prevent sensitive responses
     * from being cached by browsers or intermediaries.
     *
     * @param response HTTP response object
     * @param logKey unique request identifier used for logging
     */
    public void addNoStoreHeaders(HttpServletResponse response,String logKey) {
        logger.info("LogKey: {} - Adding no-store cache headers", logKey);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Pragma", "no-cache");
    }


}

