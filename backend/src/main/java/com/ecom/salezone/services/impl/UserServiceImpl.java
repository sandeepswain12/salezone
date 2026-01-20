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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto createUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        User user = dtoToEntity(userDto);
        Role roleUser = roleRepository.findById("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));

        logger.info("ROLE USER CREATED --------------> :{}", roleUser);

        user.getRoles().add(roleUser);
        User savedUser = userRepository.save(user);

        return entityToDto(savedUser);
    }


    @Override
    public UserDto updateUser(UserDto updatedUserDto, String userId) {
        User exuser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        exuser.setUserName(updatedUserDto.getUserName());
        exuser.setEmail(updatedUserDto.getEmail());
        exuser.setPassword(updatedUserDto.getPassword());
        exuser.setAbout(updatedUserDto.getAbout());
        exuser.setGender(updatedUserDto.getGender());
        exuser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        exuser.setImageName(updatedUserDto.getImageName());
        User savedUser = userRepository.save(exuser);
        return entityToDto(savedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? (Sort.by(sortBy)).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);
        return response;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found......!!!"));
        return entityToDto(user);
    }

    @Override
    public List<UserDto> searchUsers(String keyword) {
        List<User> users = userRepository.findByUserNameContaining(keyword);
        return users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    public User dtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto entityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
