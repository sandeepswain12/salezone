package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.AddressType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    private String mobile;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Size(min = 2, max = 50, message = "City must be between 2 to 50 characters")
    private String city;

    @Size(min = 2, max = 50, message = "State must be between 2 to 50 characters")
    private String state;

    @Size(min = 10, max = 200, message = "Address must be between 10 to 200 characters")
    private String fullAddress;

    private AddressType addressType;

    private Boolean isDefault;
}
