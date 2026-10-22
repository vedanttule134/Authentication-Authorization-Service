package com.security.SpringSecurityApplication.serviceImpl;

import com.security.SpringSecurityApplication.entities.Users;
import com.security.SpringSecurityApplication.repositories.UserRepository;
import com.security.SpringSecurityApplication.requestDTO.CreateUserRequest;
import com.security.SpringSecurityApplication.responseDTO.CreateUserResponse;
import com.security.SpringSecurityApplication.responseDTO.GetAllUsersResponse;
import com.security.SpringSecurityApplication.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public CreateUserResponse createNewUser(CreateUserRequest userDetails) {

        Users user = modelMapper.map(userDetails, Users.class);
        if (userRepo.existsByUsername(userDetails.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if(userRepo.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email Already Registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Users saved = userRepo.save(user);

        return modelMapper.map(saved, CreateUserResponse.class);
    }

    @Override
    public List<GetAllUsersResponse> getAllUsers() {
        List<Users> list = userRepo.findAll();
        return list.stream()
                .map(n -> modelMapper.map(n, GetAllUsersResponse.class))
                .toList();
    }
}
