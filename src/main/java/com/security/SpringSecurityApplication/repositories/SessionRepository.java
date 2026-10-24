package com.security.SpringSecurityApplication.repositories;

import com.security.SpringSecurityApplication.entities.Sessions;
import com.security.SpringSecurityApplication.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Sessions, Long> {

    List<Sessions> findByUser(Users user);

    Sessions findByRefreshToken(String refreshToken);

    @Modifying
    @Query("DELETE FROM Sessions r WHERE r.refreshToken = :refreshToken")
    void deleteByRefreshToken(
            @Param("refreshToken") String refreshToken
    );
}
