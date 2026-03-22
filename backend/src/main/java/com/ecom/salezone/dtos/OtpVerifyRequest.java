package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.OtpType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;

    @NotNull
    private OtpType type;

    // Required only when type = LOGIN
    private String preAuthToken;
}