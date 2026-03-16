package com.ecom.salezone.controller;

import com.ecom.salezone.dtos.*;
import com.ecom.salezone.services.CloudnaryImageService;
import com.ecom.salezone.services.FileService;
import com.ecom.salezone.services.UserService;
import com.ecom.salezone.util.LogKeyGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UserController handles user management operations
 * in the SaleZone E-commerce system.
 *
 * This controller provides APIs for:
 * - Fetching users
 * - Searching users
 * - Updating user profile
 * - Deleting users
 * - Uploading user profile images
 * - Serving user profile images
 *
 * Features:
 * - Pagination support for user listing
 * - User search functionality
 * - Profile image upload using Cloudinary
 * - Profile image retrieval
 *
 * Security:
 * - Some endpoints may require admin privileges.
 * - Profile update operations are user specific.
 *
 * Image Handling:
 * - Profile images are uploaded to Cloudinary.
 * - Image URLs are stored in the database.
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 15-03-2026
 */

@Tag(
        name = "User APIs",
        description = "APIs for managing users in the SaleZone e-commerce system"
)
@RestController
@RequestMapping("/salezone/ecom/users")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private CloudnaryImageService cloudnaryImageService;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Operation(
            summary = "Get all users",
            description = "Fetches all users with pagination and sorting support."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
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

    @Operation(
            summary = "Get user by ID",
            description = "Fetches a specific user using the user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable String userId
    ) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get user by ID request received | userId={}",
                logKey, userId);

        UserDto userDto =
                userService.getUserById(userId, logKey);

        log.info("LogKey: {} - User fetched successfully | userId={} payload={}",
                logKey, userId, userDto);

        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Get user by email",
            description = "Fetches a user using their registered email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(
            @PathVariable String email
    ) {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Get user by email request received | email={}",
                logKey, email);

        UserDto userDto =
                userService.getUserByEmail(email, logKey);

        log.info("LogKey: {} - User fetched successfully by email | email={} payload={}",
                logKey, email, userDto);

        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Search users",
            description = "Search users using keywords such as username or email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully")
    })
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUsers(
            @PathVariable String keywords)
    {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Search users request received | keywords={}",
                logKey, keywords);

        List<UserDto> users =
                userService.searchUsers(keywords, logKey);

        log.info("LogKey: {} - User search completed successfully | resultCount={}",
                logKey, users.size());

        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Update user",
            description = "Updates user profile information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest userDto)
    {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Update user request received | userId={} payload={}",
                logKey, userId, userDto);

        UserDto updatedUser =
                userService.updateUser(userDto, userId, logKey);

        log.info("LogKey: {} - User updated successfully | userId={} payload={}",
                logKey, userId, updatedUser);

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(
            @PathVariable String userId)
    {

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

    @Operation(
            summary = "Upload user profile image",
            description = "Uploads a user profile image to Cloudinary and updates the user profile."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId) throws IOException
    {

        String logKey = LogKeyGenerator.generateLogKey();

        log.info("LogKey: {} - Upload user image request received | userId={} fileName={}",
                logKey, userId, image.getOriginalFilename());

        /* Before we store image in application
        String imageName =
                fileService.uploadFile(image, imageUploadPath, logKey);

        UserDto user =
                userService.getUserById(userId, logKey);

        user.setImageName(imageName);
        userService.updateUser(user, userId, logKey);*/

        String userImageUrl = cloudnaryImageService.uploadImage(image, logKey);

        UserDto userDto = userService.getUserById(userId, logKey);
        userDto.setImageName(userImageUrl);
        UpdateUserRequest updateUserRequest = mapper.map(userDto, UpdateUserRequest.class);
        userService.updateUser(updateUserRequest, userId, logKey);


        ImageResponse imageResponse =
                ImageResponse.builder()
                        .imageName(userImageUrl)
                        .success(true)
                        .message("Image uploaded successfully")
                        .status(HttpStatus.CREATED)
                        .build();

        log.info("LogKey: {} - User image uploaded successfully | userId={} imageName={}",
                logKey, userId, userImageUrl);

        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Serve user image",
            description = "Streams the user profile image."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image served successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable String userId,
            HttpServletResponse response) throws IOException
    {

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