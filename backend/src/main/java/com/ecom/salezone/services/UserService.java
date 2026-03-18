package com.ecom.salezone.services;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UpdateUserRequest;
import com.ecom.salezone.dtos.UserDto;

import java.util.List;

/**
 * UserService defines business operations related to
 * user management in the SaleZone E-commerce system.
 *
 * Responsibilities:
 * - Creating new users
 * - Updating user profiles
 * - Deleting users
 * - Fetching user information
 * - Searching users
 * - Validating user existence
 *
 * This service acts as the business layer between
 * controllers and repositories for user-related operations.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param userDto user registration details
     * @param logkey unique request identifier used for tracing logs
     *
     * @return created user details
     */
    UserDto createUser(SignupRequestDto userDto, String logkey);

    /**
     * Updates an existing user.
     *
     * @param userDto updated user details
     * @param userId ID of the user to update
     * @param logkey unique request identifier used for tracing logs
     *
     * @return updated user details
     */
    UserDto updateUser(UpdateUserRequest userDto, String userId, String logkey);

    /**
     * Deletes a user from the system.
     *
     * @param userId ID of the user to delete
     * @param logkey unique request identifier used for tracing logs
     */
    void deleteUser(String userId, String logkey);

    /**
     * Fetches all users with pagination and sorting.
     *
     * @param pageNumber page index
     * @param pageSize number of records per page
     * @param sortBy field used for sorting
     * @param sortDir sorting direction (asc / desc)
     * @param logkey unique request identifier used for tracing logs
     *
     * @return paginated list of users
     */
    PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey
    );

    /**
     * Fetches a user using user ID.
     *
     * @param userId ID of the user
     * @param logkey unique request identifier used for tracing logs
     *
     * @return user details
     */
    UserDto getUserById(String userId, String logkey);

    /**
     * Fetches a user using email address.
     *
     * @param email registered user email
     * @param logkey unique request identifier used for tracing logs
     *
     * @return user details
     */
    UserDto getUserByEmail(String email, String logkey);

    /**
     * Searches users by keyword such as username or email.
     *
     * @param keyword search keyword
     * @param logkey unique request identifier used for tracing logs
     *
     * @return list of matching users
     */
    List<UserDto> searchUsers(String keyword, String logkey);

    /**
     * Checks whether a user exists with the given email.
     *
     * Used internally for validation during registration
     * or profile updates.
     *
     * @param email user email
     * @param logkey unique request identifier used for tracing logs
     *
     * @return true if user exists, otherwise false
     */
    boolean existsByEmail(String email, String logkey);
}