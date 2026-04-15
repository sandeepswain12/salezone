package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAddressRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 to 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 50, message = "State must be between 2 to 50 characters")
    private String state;

    @NotBlank(message = "Full address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 to 200 characters")
    private String fullAddress;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    private Boolean isDefault = false;
}