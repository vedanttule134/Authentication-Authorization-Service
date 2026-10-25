package com.security.SpringSecurityApplication.responseDTO;

import com.security.SpringSecurityApplication.enums.Permission;
import com.security.SpringSecurityApplication.enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CreateUserResponse {

    private String username;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Role> roles;
    private Set<Permission> permissions;
}
