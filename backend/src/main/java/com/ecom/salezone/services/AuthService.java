package com.ecom.salezone.services;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;

/**
 * AuthService defines authentication related business operations
 * for the SaleZone E-commerce system.
 *
 * This service handles user authentication workflows such as:
 * - User registration
 * - User login
 * - Authentication related validations
 *
 * Implementations of this interface will contain the business logic
 * required for managing authentication and user onboarding.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param userDto contains user registration details such as
     *                name, email, password, etc.
     * @param logkey  unique request identifier used for tracing logs
     *
     * @return UserDto containing the registered user information
     */
    UserDto registerUser(SignupRequestDto userDto, String logkey);

}