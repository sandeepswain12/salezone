package com.ecom.salezone.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendOtpEmail(String to, String userName, String otp,
                      com.ecom.salezone.enums.OtpType type, String logKey);
    void sendWelcomeEmail(String to, String userName, String logKey);
}
