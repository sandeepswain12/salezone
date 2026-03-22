package com.ecom.salezone.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreAuthResponse {
    private String preAuthToken;
    private String message;
    private String email;
}
