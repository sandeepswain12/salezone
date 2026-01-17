package com.ecom.salezone.services;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.UserDto;

import java.util.List;

public interface UserService {
    /**
     * Create a new user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Update existing user by userId
     */
    UserDto updateUser(UserDto userDto, String userId);

    /**
     * Delete user by userId
     */
    void deleteUser(String userId);

    /**
     * Get all users with pagination & sorting
     */
    PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir
    );

    /**
     * Get single user by userId
     */
    UserDto getUserById(String userId);

    /**
     * Get single user by email
     */
    UserDto getUserByEmail(String email);

    /**
     * Search users by keyword (name/email)
     */
    List<UserDto> searchUsers(String keyword);

    /**
     * Check whether user exists by email
     * (used internally for validation)
     */
    boolean existsByEmail(String email);
}
