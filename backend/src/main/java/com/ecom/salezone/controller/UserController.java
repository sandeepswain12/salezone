package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.ApiResponseMessage;
import com.ecom.salezone.dtos.ImageResponse;
import com.ecom.salezone.dtos.PageableResponse;
import com.ecom.salezone.dtos.UserDto;
import com.ecom.salezone.helper.LogKeyGenerator;
import com.ecom.salezone.services.FileService;
import com.ecom.salezone.services.UserService;
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

    // Controller-level logger
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    /**
     * Create new user
     */
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Create User | logKey={} payload={}", logKey, userDto);

        UserDto savedUser = userService.createUser(userDto, logKey);

        log.info("API RESPONSE: User Created | logKey={} userId={}",
                logKey, savedUser.getUserId());

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Get all users with pagination
     */
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUser(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userName") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Get All Users | logKey={} page={} size={} sortBy={} sortDir={}",
                logKey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<UserDto> response =
                userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir, logKey);

        log.info("API RESPONSE: Users Fetched | logKey={} count={}",
                logKey, response.getContent().size());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update user
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Update User | logKey={} userId={} payload={}",
                logKey, userId, userDto);

        UserDto updatedUser = userService.updateUser(userDto, userId, logKey);

        log.info("API RESPONSE: User Updated | logKey={} userId={}", logKey, userId);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Get User By ID | logKey={} userId={}", logKey, userId);

        UserDto userDto = userService.getUserById(userId, logKey);

        log.info("API RESPONSE: User Fetched | logKey={} userId={}", logKey, userId);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Get User By Email | logKey={} email={}", logKey, email);

        UserDto userDto = userService.getUserByEmail(email, logKey);

        log.info("API RESPONSE: User Fetched By Email | logKey={}", logKey);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * Search users
     */
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Search Users | logKey={} keywords={}", logKey, keywords);

        List<UserDto> users = userService.searchUsers(keywords, logKey);

        log.info("API RESPONSE: Search Completed | logKey={} resultCount={}",
                logKey, users.size());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) {

        String logKey = LogKeyGenerator.generateLogKey();
        log.warn("API CALL: Delete User | logKey={} userId={}", logKey, userId);

        userService.deleteUser(userId, logKey);

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("User is deleted Successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        log.info("API RESPONSE: User Deleted | logKey={} userId={}", logKey, userId);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Upload user profile image
     */
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Upload User Image | logKey={} userId={} fileName={}",
                logKey, userId, image.getOriginalFilename());

        String imageName = fileService.uploadFile(image, imageUploadPath);

        UserDto user = userService.getUserById(userId, logKey);
        user.setImageName(imageName);
        userService.updateUser(user, userId, logKey);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .message("image is uploaded successfully ")
                .status(HttpStatus.CREATED)
                .build();

        log.info("API RESPONSE: Image Uploaded | logKey={} userId={}", logKey, userId);

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    /**
     * Serve user profile image
     */
    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable String userId,
            HttpServletResponse response) throws IOException {

        String logKey = LogKeyGenerator.generateLogKey();
        log.info("API CALL: Serve User Image | logKey={} userId={}", logKey, userId);

        UserDto user = userService.getUserById(userId, logKey);

        InputStream resource =
                fileService.getResource(imageUploadPath, user.getImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

        log.info("API RESPONSE: User Image Served | logKey={} userId={}", logKey, userId);
    }
}
