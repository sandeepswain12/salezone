package com.ecom.salezone.repository;

import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email,String password);

    List<User> findByUserNameContaining(String keywords);
}
