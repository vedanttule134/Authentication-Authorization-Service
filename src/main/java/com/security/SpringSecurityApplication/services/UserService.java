package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.requestDTO.CreateUserRequest;
import com.security.SpringSecurityApplication.responseDTO.CreateUserResponse;


public interface UserService {
    CreateUserResponse createNewUser(CreateUserRequest userDetails);
}
