package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.AddAddressRequest;
import com.ecom.salezone.dtos.AddressDto;
import com.ecom.salezone.dtos.UpdateAddressRequest;
import com.ecom.salezone.services.AddressService;
import com.ecom.salezone.util.LogKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salezone/ecom/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @PostMapping("/{userId}")
    public ResponseEntity<AddressDto> addAddress(
            @PathVariable String userId,
            @RequestBody AddAddressRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();

        return ResponseEntity.ok(
                addressService.addAddress(userId, request, logkey)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressDto>> getAddresses(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();

        return ResponseEntity.ok(
                addressService.getUserAddresses(userId, logkey)
        );
    }

    @DeleteMapping("/{userId}/{addressId}")
    public ResponseEntity<?> delete(
            @PathVariable String userId,
            @PathVariable String addressId) {

        String logkey = LogKeyGenerator.generateLogKey();

        addressService.deleteAddress(userId, addressId, logkey);

        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable String userId,
            @PathVariable String addressId,
            @RequestBody UpdateAddressRequest request) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Update address request received | userId={} addressId={} payload={}",
                logkey, userId, addressId, request);

        AddressDto dto =
                addressService.updateAddress(userId, addressId, request, logkey);

        log.info("LogKey: {} - Address updated successfully | userId={} addressId={}",
                logkey, userId, addressId);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{userId}/default/{addressId}")
    public ResponseEntity<AddressDto> setDefault(
            @PathVariable String userId,
            @PathVariable String addressId) {

        String logkey = LogKeyGenerator.generateLogKey();

        return ResponseEntity.ok(
                addressService.setDefaultAddress(userId, addressId, logkey)
        );
    }

    @GetMapping("/{userId}/default")
    public ResponseEntity<AddressDto> getDefaultAddress(
            @PathVariable String userId) {

        String logkey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get default address request | userId={}",
                logkey, userId);

        AddressDto dto =
                addressService.getDefaultAddress(userId, logkey);

        return ResponseEntity.ok(dto);
    }
}