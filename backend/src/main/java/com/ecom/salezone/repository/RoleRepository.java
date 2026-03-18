package com.ecom.salezone.repository;

import com.ecom.salezone.enities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Role entities.
 *
 * Provides database operations related to roles including:
 * - Fetching role information
 * - Managing role records
 * - Basic CRUD operations
 *
 * Roles are typically used for authorization and access control
 * within the application (e.g., ADMIN, USER).
 *
 * Extends JpaRepository which automatically provides
 * standard database operations.
 *
 * @author : Sandeep Kumar Swain
 * @since : 15-03-2026
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}