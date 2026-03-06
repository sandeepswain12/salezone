package com.ecom.salezone.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateUserRequest {

    @NotBlank(message = "Username is required !!")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Email is required !!")
    @Email(message = "Invalid email format !!")
    private String email;

    @NotBlank(message = "Gender is required !!")
    @Pattern(regexp = "Male|Female", message = "Gender must be Male or Female")
    private String gender;

    @NotBlank(message = "About section cannot be empty !!")
    @Size(min = 10, max = 300, message = "About must be between 10 and 300 characters")
    private String about;

    @NotBlank(message = "Phone number is required !!")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

//    @Size(max = 255, message = "Image name too long")
    private String imageName;
}