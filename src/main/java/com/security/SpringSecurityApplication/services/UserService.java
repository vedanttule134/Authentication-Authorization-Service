package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.requestDTO.CreateUserRequest;
import com.security.SpringSecurityApplication.responseDTO.CreateUserResponse;
import com.security.SpringSecurityApplication.responseDTO.GetAllUsersResponse;

import java.util.List;


public interface UserService {
    CreateUserResponse createNewUser(CreateUserRequest userDetails);
    List<GetAllUsersResponse> getAllUsers();
}
