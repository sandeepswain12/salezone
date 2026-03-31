package com.ecom.salezone.services.impl;

import com.ecom.salezone.controller.AddressController;
import com.ecom.salezone.dtos.AddAddressRequest;
import com.ecom.salezone.dtos.AddressDto;
import com.ecom.salezone.dtos.UpdateAddressRequest;
import com.ecom.salezone.enities.Address;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.AddressRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.AddressService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    @Override
    public AddressDto addAddress(String userId, AddAddressRequest request, String logkey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If new address is default → remove old default
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Address> addresses = addressRepository.findByUser(user);
            addresses.forEach(addr -> addr.setIsDefault(false));
        }

        Address address = mapper.map(request, Address.class);
        String addressId = UUID.randomUUID().toString();
        address.setId(addressId);
        address.setUser(user);

        Address saved = addressRepository.save(address);

        return mapper.map(saved, AddressDto.class);
    }

    @Override
    public List<AddressDto> getUserAddresses(String userId, String logkey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return addressRepository.findByUser(user)
                .stream()
                .map(addr -> mapper.map(addr, AddressDto.class))
                .toList();
    }

    @Override
    public void deleteAddress(String userId, String addressId, String logkey) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.delete(address);
    }

    @Override
    public AddressDto setDefaultAddress(String userId, String addressId, String logkey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Address> addresses = addressRepository.findByUser(user);

        addresses.forEach(addr -> addr.setIsDefault(false));

        Address selected = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        selected.setIsDefault(true);

        return mapper.map(selected, AddressDto.class);
    }

    @Override
    public AddressDto getDefaultAddress(String userId, String logkey) {

        log.info("LogKey: {} - Get default address | userId={}", logkey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("User not found");
                });

        Address address = addressRepository
                .findByUserAndIsDefaultTrue(user)
                .orElseThrow(() -> {
                    log.warn("LogKey: {} - No default address found | userId={}", logkey, userId);
                    return new ResourceNotFoundException("Default address not found");
                });

        log.info("LogKey: {} - Default address fetched | addressId={}",
                logkey, address.getId());

        return mapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto updateAddress(String userId, String addressId, UpdateAddressRequest request, String logkey) {

        log.info("LogKey: {} - Update address | userId={} addressId={} payload={}",
                logkey, userId, addressId, request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // 🔥 SECURITY CHECK (VERY IMPORTANT)
        if (!address.getUser().getUserId().equals(userId)) {
            throw new BadApiRequestException("Address does not belong to this user");
        }

        // ⭐ Handle default logic
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Address> addresses = addressRepository.findByUser(user);
            addresses.forEach(addr -> addr.setIsDefault(false));
            address.setIsDefault(true);
        }

        //  Update fields
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setLandmark(request.getLandmark());

        Address updated = addressRepository.save(address);

        log.info("LogKey: {} - Address updated successfully | addressId={}",
                logkey, addressId);

        return mapper.map(updated, AddressDto.class);
    }
}
