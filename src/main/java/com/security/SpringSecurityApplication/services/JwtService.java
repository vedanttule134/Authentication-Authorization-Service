package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.entities.Users;
import com.security.SpringSecurityApplication.repositories.UserRepository;
import com.security.SpringSecurityApplication.requestDTO.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final UserRepository userRepo;

    @Value("${jwt.secretKey}")
    private String jwtSecrectKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecrectKey.getBytes(StandardCharsets.UTF_8));
    }

    public JwtService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String generateToken(LoginRequest request) {

        Users user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username does not exist!"));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*5))
                .signWith(getSecretKey())
                .compact();

    }

    public Long verifyToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}
