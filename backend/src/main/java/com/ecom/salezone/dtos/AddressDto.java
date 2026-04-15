package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.AddressType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String id;
    private String name;
    private String mobile;
    private String pincode;
    private String city;
    private String state;
    private String fullAddress;
    private AddressType addressType;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto user;
}