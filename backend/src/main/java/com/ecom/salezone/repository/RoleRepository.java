package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
}
