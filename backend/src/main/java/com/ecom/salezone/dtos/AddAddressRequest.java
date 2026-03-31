package com.ecom.salezone.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAddressRequest {

    private String fullName;
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String state;
    private String pincode;

    private String landmark;
    private Boolean isDefault;
}