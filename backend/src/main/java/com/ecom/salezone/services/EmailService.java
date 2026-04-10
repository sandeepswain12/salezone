package com.ecom.salezone.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body, String logKey);
}
