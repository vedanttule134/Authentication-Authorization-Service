package com.security.SpringSecurityApplication.requestDTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
