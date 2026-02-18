package com.ecom.salezone.services;

import com.ecom.salezone.dtos.SignupRequestDto;
import com.ecom.salezone.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(SignupRequestDto userDto,String logkey);


    //login user

}
