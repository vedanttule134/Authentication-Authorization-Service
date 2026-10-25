package com.security.SpringSecurityApplication.utils;

import com.security.SpringSecurityApplication.enums.Permission;
import com.security.SpringSecurityApplication.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.security.SpringSecurityApplication.enums.Permission.*;
import static com.security.SpringSecurityApplication.enums.Role.*;

public class permissionMappings {

    private static final Map<Role, Set<Permission>> map = Map.of(
            ADMIN, Set.of(
                    EMP_VIEW,
                    EMP_CREATE,
                    EMP_UPDATE,
                    EMP_DELETE,
                    USER_CREATE,
                    USER_VIEW
            ),
            USER, Set.of(
                    EMP_VIEW
            ),
            CREATOR, Set.of(
                    EMP_VIEW,
                    EMP_CREATE
            )
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(Role role) {
        return map.get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }
}
