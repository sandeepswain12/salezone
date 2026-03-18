package com.ecom.salezone.util;

import java.security.SecureRandom;

/**
 * Utility class for generating unique log keys used in application logging.
 *
 * Each request can generate a short random identifier (logKey)
 * which is added to log messages to make it easier to trace
 * logs belonging to the same request.
 *
 * Example logKey format:
 * [A9K2FQ]
 *
 * The key is generated using {@link SecureRandom} to ensure
 * randomness and avoid predictable patterns.
 *
 * This helps with:
 * - debugging requests
 * - tracing logs across layers (controller → service → repository)
 * - easier log filtering in log monitoring tools
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public class LogKeyGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    private LogKeyGenerator() {}

    /**
     * Generates a random log key used for request-level logging.
     *
     * The log key consists of 6 random uppercase alphanumeric
     * characters wrapped in square brackets.
     *
     * Example:
     * [A3F9KQ]
     *
     * @return generated log key string
     */
    public static String generateLogKey() {
        StringBuilder logKey = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            logKey.append(CHARACTERS.charAt(index));
        }

        return "[" + logKey + "]";
    }
}
