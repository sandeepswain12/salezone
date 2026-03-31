package com.ecom.salezone.services;

import com.ecom.salezone.dtos.AddAddressRequest;
import com.ecom.salezone.dtos.AddressDto;
import com.ecom.salezone.dtos.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

    AddressDto addAddress(String userId, AddAddressRequest request, String logkey);

    List<AddressDto> getUserAddresses(String userId, String logkey);

    void deleteAddress(String userId, String addressId, String logkey);

    AddressDto setDefaultAddress(String userId, String addressId, String logkey);

    AddressDto getDefaultAddress(String userId, String logkey);

    AddressDto updateAddress(String userId, String addressId, UpdateAddressRequest request, String logkey);
}