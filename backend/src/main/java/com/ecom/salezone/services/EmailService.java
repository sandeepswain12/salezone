package com.ecom.salezone.services;

import com.ecom.salezone.enums.OtpType;

public interface EmailService {
    void sendEmail(String to, String subject, String body, String logKey);
    void sendOtpEmail(String to, String userName, String otp,
                      OtpType type, String logKey);
    void sendWelcomeEmail(String to, String userName, String logKey);
    void sendPasswordResetOtpEmail(String to, String userName, String otp, String logKey);

    void sendPasswordResetLinkEmail(String to, String userName, String resetLink, String logKey);

    void sendPasswordChangedEmail(String to, String userName, String logKey);
}
