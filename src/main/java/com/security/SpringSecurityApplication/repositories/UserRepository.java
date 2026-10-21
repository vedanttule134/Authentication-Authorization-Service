package com.security.SpringSecurityApplication.repositories;

import com.security.SpringSecurityApplication.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);
}
