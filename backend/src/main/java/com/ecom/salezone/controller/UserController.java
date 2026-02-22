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

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "userName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get all users request received | page={} size={} sortBy={} sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<UserDto> response =
                userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir, logKey);

        log.info("LogKey: {} - Users fetched successfully | totalElements={}",
                logKey, response.getContent().size());

        return ResponseEntity.ok(response);
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get user by ID request received | userId={}",
                logKey, userId);

        UserDto userDto =
                userService.getUserById(userId, logKey);

        log.info("LogKey: {} - User fetched successfully | userId={} payload={}",
                logKey, userId, userDto);

        return ResponseEntity.ok(userDto);
    }

    // ================= GET USER BY EMAIL =================
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get user by email request received | email={}",
                logKey, email);

        UserDto userDto =
                userService.getUserByEmail(email, logKey);

        log.info("LogKey: {} - User fetched successfully by email | email={} payload={}",
                logKey, email, userDto);

        return ResponseEntity.ok(userDto);
    }

    // ================= SEARCH USERS =================
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUsers(
            @PathVariable String keywords) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Search users request received | keywords={}",
                logKey, keywords);

        List<UserDto> users =
                userService.searchUsers(keywords, logKey);

        log.info("LogKey: {} - User search completed successfully | resultCount={}",
                logKey, users.size());

        return ResponseEntity.ok(users);
    }

    // ================= UPDATE USER =================
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Update user request received | userId={} payload={}",
                logKey, userId, userDto);

        UserDto updatedUser =
                userService.updateUser(userDto, userId, logKey);

        log.info("LogKey: {} - User updated successfully | userId={} payload={}",
                logKey, userId, updatedUser);

        return ResponseEntity.ok(updatedUser);
    }

    // ================= DELETE USER =================
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(
            @PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.warn("LogKey: {} - Delete user request received | userId={}",
                logKey, userId);

        userService.deleteUser(userId, logKey);

        ApiResponseMessage message =
                ApiResponseMessage.builder()
                        .message("User is deleted Successfully !!")
                        .success(true)
                        .status(HttpStatus.OK)
                        .build();

        log.info("LogKey: {} - User deleted successfully | userId={}",
                logKey, userId);

        return ResponseEntity.ok(message);
    }

    // ================= UPLOAD USER IMAGE =================
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Upload user image request received | userId={} fileName={}",
                logKey, userId, image.getOriginalFilename());

        String imageName =
                fileService.uploadFile(image, imageUploadPath, logKey);

        UserDto user =
                userService.getUserById(userId, logKey);

        user.setImageName(imageName);
        userService.updateUser(user, userId, logKey);

        ImageResponse imageResponse =
                ImageResponse.builder()
                        .imageName(imageName)
                        .success(true)
                        .message("Image uploaded successfully")
                        .status(HttpStatus.CREATED)
                        .build();

        log.info("LogKey: {} - User image uploaded successfully | userId={} imageName={}",
                logKey, userId, imageName);

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    // ================= SERVE USER IMAGE =================
    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable String userId,
            HttpServletResponse response) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Serve user image request received | userId={}",
                logKey, userId);

        UserDto user =
                userService.getUserById(userId, logKey);

        InputStream resource =
                fileService.getResource(imageUploadPath, user.getImageName(), logKey);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

        log.info("LogKey: {} - User image served successfully | userId={} imageName={}",
                logKey, userId, user.getImageName());
    }
}