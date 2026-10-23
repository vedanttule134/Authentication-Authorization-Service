package com.security.SpringSecurityApplication.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
}
