package com.ecom.salezone.services;

import com.ecom.salezone.enums.OtpType;

public interface OtpService {
    String generateOtp(String email, OtpType type, String logKey);
    void verifyOtp(String email, String code, OtpType type, String logKey);
}
