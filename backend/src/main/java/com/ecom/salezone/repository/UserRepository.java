package com.ecom.salezone.repository;

import com.ecom.salezone.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
