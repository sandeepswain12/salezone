package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.ImageResponse;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.services.FileService;
import com.ecom.salezone.services.UserService;
import com.ecom.salezone.util.LogKeyGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/salezone/ecom/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    /**
     * Get all users with pagination and sorting.
     */
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userName") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - GetAllUsers request | page={} size={} sortBy={} sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<UserDto> response =
                userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir, logKey);

        log.info("LogKey: {} - GetAllUsers completed | resultCount={}",
                logKey, response.getContent().size());

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - GetUserById request | userId={}", logKey, userId);

        UserDto userDto = userService.getUserById(userId, logKey);

        log.info("LogKey: {} - GetUserById completed | userId={}", logKey, userId);

        return ResponseEntity.ok(userDto);
    }

    /**
     * Get user by email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - GetUserByEmail request | email={}", logKey, email);

        UserDto userDto = userService.getUserByEmail(email, logKey);

        log.info("LogKey: {} - GetUserByEmail completed", logKey);

        return ResponseEntity.ok(userDto);
    }

    /**
     * Search users using keywords.
     */
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUsers(@PathVariable String keywords) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - SearchUsers request | keywords={}", logKey, keywords);

        List<UserDto> users = userService.searchUsers(keywords, logKey);

        log.info("LogKey: {} - SearchUsers completed | resultCount={}", logKey, users.size());

        return ResponseEntity.ok(users);
    }

    /**
     * Update user.
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - UpdateUser request | userId={}", logKey, userId);

        UserDto updatedUser = userService.updateUser(userDto, userId, logKey);

        log.info("LogKey: {} - UpdateUser completed | userId={}", logKey, userId);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user by ID.
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - DeleteUser request | userId={}", logKey, userId);

        userService.deleteUser(userId, logKey);

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("User is deleted Successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        log.info("LogKey: {} - DeleteUser completed | userId={}", logKey, userId);

        return ResponseEntity.ok(message);
    }

    /**
     * Upload profile image for user.
     */
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - UploadUserImage request | userId={} fileName={}",
                logKey, userId, image.getOriginalFilename());

        String imageName = fileService.uploadFile(image, imageUploadPath, logKey);

        UserDto user = userService.getUserById(userId, logKey);
        user.setImageName(imageName);
        userService.updateUser(user, userId, logKey);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .message("Image uploaded successfully")
                .status(HttpStatus.CREATED)
                .build();

        log.info("LogKey: {} - UploadUserImage completed | userId={}", logKey, userId);

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    /**
     * Serve user profile image.
     */
    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable String userId,
            HttpServletResponse response) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - ServeUserImage request | userId={}", logKey, userId);

        UserDto user = userService.getUserById(userId, logKey);

        InputStream resource =
                fileService.getResource(imageUploadPath, user.getImageName(), logKey);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

        log.info("LogKey: {} - ServeUserImage completed | userId={}", logKey, userId);
    }
}
