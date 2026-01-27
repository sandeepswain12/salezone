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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/salezone/ecom/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    // ================= CREATE USER =================
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → CREATE USER | payload={}", logkey, userDto);

        UserDto savedUser = userService.createUser(userDto, logkey);

        logger.info("[{}] RESPONSE ← USER CREATED | userId={}", logkey, savedUser.getUserId());
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // ================= GET ALL USERS =================
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUser(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userName") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → GET ALL USERS | page={} size={} sortBy={} dir={}",
                logkey, pageNumber, pageSize, sortBy, sortDir);

        PageableResponse<UserDto> response =
                userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir, logkey);

        logger.info("[{}] RESPONSE ← USERS COUNT={}", logkey, response.getContent().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= UPDATE USER =================
    @PutMapping("update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserDto userDto
    ) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → UPDATE USER | userId={} payload={}", logkey, userId, userDto);

        UserDto updatedUser = userService.updateUser(userDto, userId, logkey);

        logger.info("[{}] RESPONSE ← USER UPDATED | userId={}", logkey, userId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → GET USER BY ID | userId={}", logkey, userId);

        UserDto userDto = userService.getUserById(userId, logkey);

        logger.info("[{}] RESPONSE ← USER FETCHED | userId={}", logkey, userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // ================= GET USER BY EMAIL =================
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → GET USER BY EMAIL | email={}", logkey, email);

        UserDto userDto = userService.getUserByEmail(email, logkey);

        logger.info("[{}] RESPONSE ← USER FETCHED BY EMAIL", logkey);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    // ================= SEARCH USERS =================
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → SEARCH USERS | keywords={}", logkey, keywords);

        List<UserDto> users = userService.searchUsers(keywords, logkey);

        logger.info("[{}] RESPONSE ← SEARCH RESULT COUNT={}", logkey, users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // ================= DELETE USER =================
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable("userId") String userId) {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.warn("[{}] REQUEST → DELETE USER | userId={}", logkey, userId);

        userService.deleteUser(userId, logkey);

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("User is deleted Successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        logger.info("[{}] RESPONSE ← USER DELETED | userId={}", logkey, userId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= UPLOAD USER IMAGE =================
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable String userId
    ) throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → UPLOAD USER IMAGE | userId={} file={}",
                logkey, userId, image.getOriginalFilename());

        String imageName = fileService.uploadFile(image, imageUploadPath);
        logger.info("[{}] IMAGE UPLOADED | imageName={}", logkey, imageName);

        UserDto user = userService.getUserById(userId, logkey);
        user.setImageName(imageName);

        UserDto updatedUser = userService.updateUser(user, userId, logkey);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .message("image is uploaded successfully ")
                .status(HttpStatus.CREATED)
                .build();

        logger.info("[{}] RESPONSE ← IMAGE UPLOAD SUCCESS | userId={}", logkey, userId);
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    // ================= SERVE USER IMAGE =================
    @GetMapping(value = "/image/{userId}")
    public void serveUserImage(@PathVariable String userId,
                               HttpServletResponse response) throws IOException {

        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("[{}] REQUEST → SERVE USER IMAGE | userId={}", logkey, userId);

        UserDto user = userService.getUserById(userId, logkey);
        logger.debug("[{}] USER IMAGE NAME={}", logkey, user.getImageName());

        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource, response.getOutputStream());

        logger.info("[{}] RESPONSE ← USER IMAGE SERVED | userId={}", logkey, userId);
    }
}
