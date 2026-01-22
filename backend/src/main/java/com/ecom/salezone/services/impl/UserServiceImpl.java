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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${user.profile.image.path}")
    private String imagePath;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto createUser(UserDto userDto, String logkey) {
        String userId = UUID.randomUUID().toString();
        logger.info("{} : USER ID CREATED : {}", logkey, userId);
        userDto.setUserId(userId);
        User user = dtoToEntity(userDto);
        logger.info("{} : USERDTO MAPPED TO USER : {}", logkey, user);
        Role roleUser = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        logger.info("{} : ROLE SET FOR USER : {}", logkey, roleUser);
        user.getRoles().add(roleUser);
        User savedUser = userRepository.save(user);
        logger.info("{} : USER SAVED : {}", logkey, savedUser);
        UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);
        logger.info("{} : USER MAPPED TO USERDTO : {}", logkey, savedUserDto);
        return savedUserDto;
    }


    @Override
    public UserDto updateUser(UserDto updatedUserDto, String userId, String logkey) {
        User exuser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        logger.info("{} : FETCHED EXISTING USER FROM DB WITH ID : {} EXISTING_USER : {}", logkey, userId, exuser);
        exuser.setUserName(updatedUserDto.getUserName());
        exuser.setEmail(updatedUserDto.getEmail());
//        if (!updatedUserDto.getPassword().equalsIgnoreCase(exuser.getPassword()))
//            exuser.setPassword(passwordEncoder.encode(updatedUserDto.getPassword()));
        exuser.setPassword(updatedUserDto.getPassword());
        exuser.setAbout(updatedUserDto.getAbout());
        exuser.setGender(updatedUserDto.getGender());
        exuser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        exuser.setImageName(updatedUserDto.getImageName());
        User savedUser = userRepository.save(exuser);
        logger.info("{} : EXISTING USER UPDATED WITH NEW DATA : {}", logkey, savedUser);
        UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);
        logger.info("{} : USER MAPPED TO USERDTO : {}", logkey, savedUserDto);
        return savedUserDto;
    }

    @Override
    public void deleteUser(String userId, String logkey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        logger.info("{} : FETCHED USER FROM DB WITH ID : {} USER : {}", logkey, userId, user);
        String fullPath = imagePath + user.getImageName();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("User image not found in folder");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRepository.delete(user);
        logger.info("{} : USER DELETED SUCCESSFULLY : {}", logkey, user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir, String logkey) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? (Sort.by(sortBy)).descending() : Sort.by(sortBy).ascending();
        logger.info("{} : USER SORTED BY ORDER AND DIRECTION : {}", logkey, sort);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        logger.info("{} : PAGEABLE OBJECT CREATE WITH PAGE REQUEST ", logkey);
        Page<User> page = userRepository.findAll(pageable);
        logger.info("{} : FETCHED ALL USERS FROM DB : {} ", logkey , page);
        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class , logkey);
        logger.info("{} : MAPPED TO PAGEABLE_RESPONSE : {} ", logkey , response);
        return response;
    }

    @Override
    public UserDto getUserById(String userId, String logkey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        logger.info("{} : FETCHED USER FROM DB WITH ID : {} USER : {}", logkey, userId, user);
        UserDto userDto = entityToDto(user);
        logger.info("{} : USER MAPPED TO USERDTO : {}", logkey, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email, String logkey) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        logger.info("{} : FETCHED USER FROM DB WITH EMAIL : {} USER : {}", logkey, email, user);
        UserDto userDto = entityToDto(user);
        logger.info("{} : USER MAPPED TO USERDTO : {}", logkey, userDto);
        return userDto;
    }

    @Override
    public List<UserDto> searchUsers(String keyword, String logkey) {
        List<User> users = userRepository.findByUserNameContaining(keyword);
        logger.info("{} : FETCHED USERS WITH KEYWORDS FROM DB WITH ID : {} USER : {}", logkey, keyword, users);
        List<UserDto> userDtos = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        logger.info("{} : LIST OF USERS ARE MAPPED TO LIST USERDTOS : {}", logkey, userDtos);
        return userDtos;
    }

    @Override
    public boolean existsByEmail(String email, String logkey) {
        return false;
    }

    public User dtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto entityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
