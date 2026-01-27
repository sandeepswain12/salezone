package com.ecom.salezone.services.impl;

import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.enities.Role;
import com.ecom.salezone.enities.User;
import com.ecom.salezone.exceptions.ResourceNotFoundException;
import com.ecom.salezone.helper.Helper;
import com.ecom.salezone.repository.RoleRepository;
import com.ecom.salezone.repository.UserRepository;
import com.ecom.salezone.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

    // ================= CREATE USER =================
    @Override
    public UserDto createUser(UserDto userDto, String logkey) {

        String userId = UUID.randomUUID().toString();
        logger.info("[{}] GENERATED USER ID={}", logkey, userId);

        userDto.setUserId(userId);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user = dtoToEntity(userDto);
        logger.debug("[{}] USERDTO → USER ENTITY | {}", logkey, user);

        Role roleUser = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        logger.info("[{}] ROLE FETCHED FOR USER={}", logkey, roleUser.getRoleName());

        user.getRoles().add(roleUser);

        User savedUser = userRepository.save(user);
        logger.info("[{}] USER SAVED IN DB | userId={}", logkey, savedUser.getUserId());

        UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);
        logger.debug("[{}] USER ENTITY → USERDTO | {}", logkey, savedUserDto);

        return savedUserDto;
    }

    // ================= UPDATE USER =================
    @Override
    public UserDto updateUser(UserDto updatedUserDto, String userId, String logkey) {

        User exuser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));

        logger.info("[{}] EXISTING USER FETCHED | userId={} user={}", logkey, userId, exuser);

        exuser.setUserName(updatedUserDto.getUserName());
        exuser.setEmail(updatedUserDto.getEmail());
        if (!updatedUserDto.getPassword().equalsIgnoreCase(exuser.getPassword()))
            exuser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
        exuser.setAbout(updatedUserDto.getAbout());
        exuser.setGender(updatedUserDto.getGender());
        exuser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        exuser.setImageName(updatedUserDto.getImageName());

        User savedUser = userRepository.save(exuser);
        logger.info("[{}] USER UPDATED SUCCESSFULLY | userId={}", logkey, savedUser.getUserId());

        UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);
        logger.debug("[{}] UPDATED USER → USERDTO | {}", logkey, savedUserDto);

        return savedUserDto;
    }

    // ================= DELETE USER =================
    @Override
    public void deleteUser(String userId, String logkey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));

        logger.info("[{}] USER FETCHED FOR DELETE | userId={} user={}", logkey, userId, user);

        String fullPath = imagePath + user.getImageName();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
            logger.info("[{}] USER IMAGE DELETED | path={}", logkey, fullPath);
        } catch (NoSuchFileException ex) {
            logger.warn("[{}] USER IMAGE NOT FOUND | path={}", logkey, fullPath);
        } catch (IOException e) {
            logger.error("[{}] ERROR DELETING USER IMAGE | path={}", logkey, fullPath, e);
        }

        userRepository.delete(user);
        logger.info("[{}] USER DELETED SUCCESSFULLY | userId={}", logkey, userId);
    }

    // ================= GET ALL USERS =================
    @Override
    public PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String logkey) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        logger.info("[{}] SORT APPLIED | {}", logkey, sort);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        logger.debug("[{}] PAGE REQUEST CREATED | page={} size={}", logkey, pageNumber, pageSize);

        Page<User> page = userRepository.findAll(pageable);
        logger.info("[{}] USERS FETCHED FROM DB | count={}", logkey, page.getNumberOfElements());

        PageableResponse<UserDto> response =
                Helper.getPageableResponse(page, UserDto.class, logkey);

        logger.debug("[{}] PAGEABLE RESPONSE CREATED", logkey);

        return response;
    }

    // ================= GET USER BY ID =================
    @Override
    public UserDto getUserById(String userId, String logkey) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));

        logger.info("[{}] USER FETCHED BY ID | userId={}", logkey, userId);

        UserDto userDto = entityToDto(user);
        logger.debug("[{}] USER → USERDTO | {}", logkey, userDto);

        return userDto;
    }

    // ================= GET USER BY EMAIL =================
    @Override
    public UserDto getUserByEmail(String email, String logkey) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));

        logger.info("[{}] USER FETCHED BY EMAIL | email={}", logkey, email);

        UserDto userDto = entityToDto(user);
        logger.debug("[{}] USER → USERDTO | {}", logkey, userDto);

        return userDto;
    }

    // ================= SEARCH USERS =================
    @Override
    public List<UserDto> searchUsers(String keyword, String logkey) {

        List<User> users = userRepository.findByUserNameContaining(keyword);
        logger.info("[{}] USERS FETCHED BY SEARCH | keyword={} count={}",
                logkey, keyword, users.size());

        List<UserDto> userDtos = users.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        logger.debug("[{}] USERS LIST MAPPED TO USERDTOS | count={}",
                logkey, userDtos.size());

        return userDtos;
    }

    @Override
    public boolean existsByEmail(String email, String logkey) {
        logger.debug("[{}] CHECK EXISTS BY EMAIL | email={}", logkey, email);
        return false;
    }

    // ================= MAPPER METHODS =================
    public User dtoToEntity(UserDto userDto) {
        logger.debug("Mapping UserDto to User entity");
        return modelMapper.map(userDto, User.class);
    }

    public UserDto entityToDto(User user) {
        logger.debug("Mapping User entity to UserDto");
        return modelMapper.map(user, UserDto.class);
    }
}
