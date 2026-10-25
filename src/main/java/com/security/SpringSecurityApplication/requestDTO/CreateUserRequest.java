package com.security.SpringSecurityApplication.requestDTO;

import com.security.SpringSecurityApplication.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
}
