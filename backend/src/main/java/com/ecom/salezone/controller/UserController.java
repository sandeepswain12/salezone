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

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR CREATE USER --> , USER_DTO : {}" , logkey , userDto);
        UserDto savedUser = userService.createUser(userDto, logkey);
        logger.info("{} : RESPONSE AFTER USER CREATION --> , USER_DTO : {}" , logkey , userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUser(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR GET ALL USERS --> , PAGE_NUMBER : {} PAGE_SIZE: {} SORT_BY : {} SORT_DIR :{}" , logkey , pageNumber, pageSize, sortBy, sortDir);
        PageableResponse<UserDto> userDtoPageableResponse = userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir, logkey);
        logger.info("{} : RESPONSE OF ALL USERS --> , USER_DTO_PAGEABLE_RESPONSE : {} " , logkey , userDtoPageableResponse);
        return new ResponseEntity<>(userDtoPageableResponse, HttpStatus.OK);
    }

    @PutMapping("update/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserDto userDto
    ) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR UPDATE --> , USERID : {}, USER_DTO : {} ", logkey , userId, userDto);
        UserDto updatedUser = userService.updateUser(userDto, userId, logkey);
        logger.info("{} : RESPONSE OF UPDATED USERS --> , UPDATED_USER : {} " , logkey , updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") String userId) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR GET USER BY ID --> , USERID : {}", logkey , userId);
        UserDto userDto = userService.getUserById(userId, logkey);
        logger.info("{} : RESPONSE OF GET USER BY ID --> , USER_DTO : {}", logkey , userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR GET USER BY EMAIL --> , EMAIL : {}", logkey , email);
        UserDto userDto = userService.getUserByEmail(email, logkey);
        logger.info("{} : RESPONSE OF GET USER BY EMAIL --> , USER_DTO : {}", logkey , userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR SEARCH USER BY KEYWORDS --> , KEYWORDS : {}", logkey , keywords);
        List<UserDto> users = userService.searchUsers(keywords, logkey);
        logger.info("{} : RESPONSE OF SEARCH USER BY KEYWORDS --> , USERS : {}", logkey , users);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable("userId") String userId) {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR DELETE --> , USERID : {}", logkey , userId);
        userService.deleteUser(userId, logkey);
        ApiResponseMessage message
                = ApiResponseMessage
                .builder()
                .message("User is deleted Successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        logger.info("{} : RESPONSE OF DELETE --> , DELETE_RESPONSE : {}", logkey , message);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image, @PathVariable String userId) throws IOException {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR UPLOAD USER IMAGE --> , IMAGE : {} , USERID : {} ", logkey , image , userId);
        String imageName = fileService.uploadFile(image, imageUploadPath);
        logger.info("{} : IMAGE UPLOADED SUCCESSFULLY --> , IMAGE : {} ", logkey , imageName);
        UserDto user = userService.getUserById(userId, logkey);
        user.setImageName(imageName);
        logger.info("{} : USER IMAGE NAME UPDATED {}: <-- TO --> : {} ", logkey , user.getImageName(), imageName);
        UserDto userDto = userService.updateUser(user, userId, logkey);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).message("image is uploaded successfully ").status(HttpStatus.CREATED).build();
        logger.info("{} : RESPONSE UPLOAD USER IMAGE --> , IMAGE RESPONSE : {}", logkey , imageResponse);
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);

    }

    //serve user image
    @GetMapping(value = "/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        String logkey = LogKeyGenerator.generateLogKey();
        logger.info("{} : REQUEST FOR SERVE USER IMAGE --> , USERID : {} , RESPONSE : {} ", logkey, userId , response);
        UserDto user = userService.getUserById(userId, logkey);
        logger.info("{} : USER IMAGE NAME : {} ", logkey, user.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
        logger.info("{} : USER IMAGE RESOURCE : {} ", logkey, resource);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
        logger.info("{} : USER IMAGE RESPONSE : {} ", logkey , StreamUtils.copy(resource, response.getOutputStream()));
    }


}
