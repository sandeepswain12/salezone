package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.Role;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    // Logger for user service operations
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${user.profile.image.path}")
    private String imagePath;

    /**
     * Create new user
     */
    @Override
    public UserDto createUser(SignupRequestDto userDto, String logKey) {

        log.info("{} Create user request received ", logKey);

        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);

        log.info("{} User name received | username={}",logKey, userDto.getUserName());

        log.info("{} Generated userId | userId={}",logKey, userId);

        User user = modelMapper.map(userDto, User.class);
        user.setUserName(user.getUserName());

        log.info("{} UserDto mapped to User | username ={}",logKey, user.getUsername());

        // Fetch ROLE_USER
        Role roleUser = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("{} ROLE_USER not found ", logKey);
                    return new ResourceNotFoundException("Role USER not found");
                });

        user.getRoles().add(roleUser);

        User savedUser = userRepository.save(user);

        log.info("{} User saved | savedUser={}",logKey, savedUser);
        log.info("{} User created successfully | userId={}",
                logKey, savedUser.getUserId());

        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Update existing user
     */
    @Override
    public UserDto updateUser(UserDto updatedUserDto, String userId, String logKey) {

        log.info("{} Update user request | userId={}",logKey, userId);

        User exUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found for update | userId={}",logKey, userId);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        exUser.setUserName(updatedUserDto.getUserName());
        exUser.setEmail(updatedUserDto.getEmail());

        if (!updatedUserDto.getPassword().equalsIgnoreCase(exUser.getPassword())) {
            exUser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
        }

        exUser.setAbout(updatedUserDto.getAbout());
        exUser.setGender(updatedUserDto.getGender());
        exUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        exUser.setImageName(updatedUserDto.getImageName());

        User savedUser = userRepository.save(exUser);

        log.info("{} User updated successfully | userId={}",
                logKey, savedUser.getUserId());

        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Delete user
     */
    @Override
    public void deleteUser(String userId, String logKey) {

        log.info("{} Delete user request | userId={} ",logKey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found for delete | userId={}",logKey, userId);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        // Delete user profile image if exists
        String fullPath = imagePath + user.getImageName();

        try {
            Files.delete(Paths.get(fullPath));
            log.info("{} User image deleted | path={}",logKey, fullPath);
        } catch (NoSuchFileException ex) {
            log.warn("{} User image not found | path={}",logKey, fullPath);
        } catch (IOException e) {
            log.error("{} Error deleting user image | path={}",logKey, fullPath, e);
        }

        userRepository.delete(user);

        log.info("{} User deleted successfully | userId={}",logKey, userId);
    }

    /**
     * Get all users with pagination
     */
    @Override
    public PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logKey) {

        log.info("{} Fetch all users | page={}, size={}, sortBy={}, sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);

        log.info("{} Users fetched | count={}",
                logKey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, UserDto.class, logKey);
    }

    /**
     * Get user by ID
     */
    @Override
    public UserDto getUserById(String userId, String logKey) {

        log.info("{} Fetch user by id | userId={}",logKey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("{} User not found | userId={}",logKey, userId);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Get user by email
     */
    @Override
    public UserDto getUserByEmail(String email, String logKey) {

        log.info("{} Fetch user by email | email={} ",logKey, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("{} User not found | email={}",logKey, email);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Search users by keyword
     */
    @Override
    public List<UserDto> searchUsers(String keyword, String logKey) {

        log.info("{} Search users | keyword={}, logKey={}",logKey, keyword, logKey);

        List<User> users = userRepository.findByUserNameContaining(keyword);

        log.info("{} Users found | keyword={}, count={}",logKey, keyword, users.size());

        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email, String logKey) {

        log.info("{} Check email existence | email={}", logKey, email);
        return false;
    }
}
