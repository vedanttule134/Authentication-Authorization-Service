package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.entities.Users;
import com.security.SpringSecurityApplication.repositories.SessionRepository;
import com.security.SpringSecurityApplication.repositories.UserRepository;
import com.security.SpringSecurityApplication.requestDTO.LoginRequest;
import com.security.SpringSecurityApplication.responseDTO.LoginResponse;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepo, ModelMapper modelMapper, SessionService sessionService, SessionRepository sessionRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.sessionService = sessionService;

        this.sessionRepository = sessionRepository;
    }

    public LoginResponse loginService(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String username = authentication.getName();
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());

        sessionService.generateNewSession(user, refreshToken);

        return new LoginResponse(request.getUsername(), accessToken, refreshToken);
    }

    public LoginResponse refreshToken(String token) {
        Long userId = jwtService.verifyToken(token);
        sessionService.validateSession(token);
        Users user = userRepo.findById(userId).orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));
        String accessToken = jwtService.generateAccessToken(user.getUsername());

        return new LoginResponse(user.getUsername(), accessToken, token);
    }

    @Transactional
    public void logout(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
    }
}
