package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.AddAddressRequest;
import com.ecom.salezone.dtos.AddressDto;
import com.ecom.salezone.dtos.UpdateAddressRequest;
import com.ecom.salezone.enities.Address;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.BadApiRequestException;
import com.ecom.salezone.repository.AddressRepository;
import com.ecom.salezone.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void addAddress_shouldUnsetExistingDefaultsAndSaveNewAddress() {
        User user = createUser("user-1");
        Address existingAddress = Address.builder().id("addr-old").user(user).isDefault(true).build();
        AddAddressRequest request = new AddAddressRequest(
                "John Doe", "9999999999", "Line 1", "Line 2",
                "Bhubaneswar", "Odisha", "751001", "Near Park", true
        );
        Address mappedAddress = new Address();
        Address savedAddress = Address.builder().id("addr-new").user(user).isDefault(true).build();
        AddressDto response = AddressDto.builder().id("addr-new").isDefault(true).build();

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(List.of(existingAddress));
        when(mapper.map(request, Address.class)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(savedAddress);
        when(mapper.map(savedAddress, AddressDto.class)).thenReturn(response);

        AddressDto result = addressService.addAddress("user-1", request, "log-1");

        assertEquals("addr-new", result.getId());
        assertEquals(Boolean.FALSE, existingAddress.getIsDefault());
        verify(addressRepository).save(mappedAddress);
    }

    @Test
    void updateAddress_shouldThrowWhenAddressBelongsToAnotherUser() {
        User user = createUser("user-1");
        User otherUser = createUser("user-2");
        Address address = Address.builder().id("addr-1").user(otherUser).build();
        UpdateAddressRequest request = new UpdateAddressRequest();

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(addressRepository.findById("addr-1")).thenReturn(Optional.of(address));

        BadApiRequestException exception = assertThrows(
                BadApiRequestException.class,
                () -> addressService.updateAddress("user-1", "addr-1", request, "log-1")
        );

        assertEquals("Address does not belong to this user", exception.getMessage());
    }

    private User createUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(userId + "@salezone.com");
        return user;
    }
}
