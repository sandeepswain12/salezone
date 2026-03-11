package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.enities.Role;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.enums.Provider;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.repository.RefreshTokenRepository;
import com.ecom.salezone.util.Helper;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${user.profile.image.path}")
    private String imagePath;

    // ================= CREATE USER =================
    @CacheEvict(
            value = {"users","users_page","users_by_email","search_users"},
            condition = "@cacheFlags.userCacheEnabled()",
            allEntries = true
    )
    @Override
    public UserDto createUser(SignupRequestDto userDto, String logKey) {

        log.info("LogKey: {} - Entry into createUser method", logKey);

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String userId = UUID.randomUUID().toString();
        log.info("LogKey: {} - User id generated | userId={}", logKey, userId);

        userDto.setUserId(userId);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        log.info("LogKey: {} - Password encrypted successfully", logKey);

        User user = modelMapper.map(userDto, User.class);
        user.setUserName(user.getUserName());
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
        user.setRoles(new HashSet<>());
        Role roleUser = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("LogKey: {} - ROLE_USER not found", logKey);
                    return new ResourceNotFoundException("Role USER not found");
                });

        user.getRoles().add(roleUser);
        log.info("LogKey: {} - User role set | role={}", logKey, roleUser);

        User savedUser = userRepository.save(user);
        log.info("LogKey: {} - User saved successfully | userId={}", logKey, savedUser.getUserId());

        return modelMapper.map(savedUser, UserDto.class);
    }

    // ================= UPDATE USER =================
    @CacheEvict(
            value = {"users","users_page","users_by_email","search_users"},
            condition = "@cacheFlags.userCacheEnabled()",
            allEntries = true
    )
    @Override
    public UserDto updateUser(UpdateUserRequest updatedUserDto, String userId, String logKey) {

//        log.info("LogKey: {} - Entry into updateUser method | userId={}", logKey, userId);
//
//        User exUser = userRepository.findById(userId)
//                .orElseThrow(() -> {
//                    log.error("LogKey: {} - User not found for update | userId={}", logKey, userId);
//                    return new ResourceNotFoundException("User not found......!!!");
//                });
//
//        exUser.setUserName(updatedUserDto.getUserName());
//        exUser.setEmail(updatedUserDto.getEmail());
//
//        if (updatedUserDto.getPassword() != null &&
//                !updatedUserDto.getPassword().isBlank() &&
//                !passwordEncoder.matches(updatedUserDto.getPassword(), exUser.getPassword())) {
//
//            exUser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
//        }
//
//        // ROLE UPDATE SECTION
//        if (updatedUserDto.getRoles() != null &&
//                !updatedUserDto.getRoles().isEmpty()) {
//
//            Set<Role> updatedRoles = new HashSet<>();
//
//            for (RoleDto roleDto : updatedUserDto.getRoles()) {
//
//                Role role = roleRepository.findById(roleDto.getRoleId())
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException("Role not found: " + roleDto.getRoleId()));
//
//                updatedRoles.add(role);
//            }
//
//            exUser.setRoles(updatedRoles);
//        }
//
//        exUser.setAbout(updatedUserDto.getAbout());
//        exUser.setGender(updatedUserDto.getGender());
//        exUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
//        exUser.setImageName(updatedUserDto.getImageName());
//
//        User savedUser = userRepository.save(exUser);
//
//        log.info("LogKey: {} - User updated successfully | userId={}", logKey, savedUser.getUserId());
//
//        return modelMapper.map(savedUser, UpdateUserRequest.class);

        User exUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        exUser.setUserName(updatedUserDto.getUserName());
        exUser.setEmail(updatedUserDto.getEmail());
        exUser.setGender(updatedUserDto.getGender());
        exUser.setAbout(updatedUserDto.getAbout());
        exUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        exUser.setImageName(updatedUserDto.getImageName());

        User savedUser = userRepository.save(exUser);

        return modelMapper.map(exUser, UserDto.class);
    }

    // ================= DELETE USER =================
    @CacheEvict(value = {"users","users_page","users_by_email","search_users"},
            condition = "@cacheFlags.userCacheEnabled()",
            allEntries = true
    )
    @Override
    public void deleteUser(String userId, String logKey) {

        log.warn("LogKey: {} - Entry into deleteUser method | userId={}", logKey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found for delete | userId={}", logKey, userId);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        //Delete refresh tokens first
//        refreshTokenRepository.deleteByUser(user);
//        log.info("LogKey: {} - Refresh tokens deleted for user | userId={}", logKey, userId);

        String fullPath = imagePath + user.getImageName();

        try {
            Files.delete(Paths.get(fullPath));
            log.info("LogKey: {} - User image deleted | path={}", logKey, fullPath);
        } catch (NoSuchFileException ex) {
            log.warn("LogKey: {} - User image not found | path={}", logKey, fullPath);
        } catch (IOException e) {
            log.error("LogKey: {} - Error deleting user image | path={}", logKey, fullPath, e);
        }

        userRepository.delete(user);

        log.info("LogKey: {} - User deleted successfully | userId={}", logKey, userId);
    }

    // ================= GET ALL USERS =================
    @Cacheable(
            value = "users_page",
            key = "'page_' + #pageNumber + '_size_' + #pageSize + '_sort_' + #sortBy + '_' + #sortDir",
            condition = "@cacheFlags.userCacheEnabled()"
    )
    @Override
    public PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logKey) {

        log.info("LogKey: {} - Entry into getAllUsers method | page={} size={} sortBy={} sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);

        log.info("LogKey: {} - Users fetched from DB | count={}",
                logKey, page.getNumberOfElements());

        return Helper.getPageableResponse(page, UserDto.class, logKey);
    }

    // ================= GET USER BY ID =================
    @Cacheable(
            value = "users",
            key = "#userId",
            condition = "@cacheFlags.userCacheEnabled()"
    )
    @Override
    public UserDto getUserById(String userId, String logKey) {

        log.info("LogKey: {} - Entry into getUserById method | userId={}", logKey, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | userId={}", logKey, userId);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        return modelMapper.map(user, UserDto.class);
    }

    // ================= GET USER BY EMAIL =================
    @Cacheable(
            value = "users_by_email",
            key = "#email",
            condition = "@cacheFlags.userCacheEnabled()"
    )
    @Override
    public UserDto getUserByEmail(String email, String logKey) {

        log.info("LogKey: {} - Entry into getUserByEmail method | email={}", logKey, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("LogKey: {} - User not found | email={}", logKey, email);
                    return new ResourceNotFoundException("User not found......!!!");
                });

        return modelMapper.map(user, UserDto.class);
    }

    // ================= SEARCH USERS =================
    @Cacheable(
            value = "search_users",
            key = "#keyword",
            condition = "@cacheFlags.userCacheEnabled()"
    )
    @Override
    public List<UserDto> searchUsers(String keyword, String logKey) {

        log.info("LogKey: {} - Entry into searchUsers method | keyword={}", logKey, keyword);

        List<User> users = userRepository.findByUserNameContaining(keyword);

        log.info("LogKey: {} - Users found | keyword={} count={}", logKey, keyword, users.size());

        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    // ================= CHECK EMAIL EXISTS =================
    @Override
    public boolean existsByEmail(String email, String logKey) {

        log.info("LogKey: {} - Entry into existsByEmail method | email={}", logKey, email);

        return false;
    }
}
