package com.ecom.salezone.helper;

import java.security.SecureRandom;


public class LogKeyGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateLogKey() {
        StringBuilder logKey = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            logKey.append(CHARACTERS.charAt(index));
        }

        return "[" + logKey + "]";
    }
}
