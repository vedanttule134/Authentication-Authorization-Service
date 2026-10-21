package com.security.SpringSecurityApplication.controllers;

import com.security.SpringSecurityApplication.requestDTO.CreateUserRequest;
import com.security.SpringSecurityApplication.responseDTO.CreateUserResponse;
import com.security.SpringSecurityApplication.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest userDetails) {

        CreateUserResponse res = userService.createNewUser(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(res);
    }
}
