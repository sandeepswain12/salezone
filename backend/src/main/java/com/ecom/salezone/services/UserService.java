package com.ecom.salezone.services;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;

import java.util.List;

public interface UserService {
    /**
     * Create a new user
     */
    UserDto createUser(SignupRequestDto userDto , String logkey);

    /**
     * Update existing user by userId
     */
    UserDto updateUser(UserDto userDto, String userId, String logkey);

    /**
     * Delete user by userId
     */
    void deleteUser(String userId, String logkey);

    /**
     * Get all users with pagination & sorting
     */
    PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Get single user by userId
     */
    UserDto getUserById(String userId, String logkey);

    /**
     * Get single user by email
     */
    UserDto getUserByEmail(String email, String logkey);

    /**
     * Search users by keyword (name/email)
     */
    List<UserDto> searchUsers(String keyword, String logkey);

    /**
     * Check whether user exists by email
     * (used internally for validation)
     */
    boolean existsByEmail(String email, String logkey);
}
