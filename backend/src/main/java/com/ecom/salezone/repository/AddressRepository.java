package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Address;
import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {

    List<Address> findByUser(User user);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    List<Address> findByUserUserId(String userId);

}