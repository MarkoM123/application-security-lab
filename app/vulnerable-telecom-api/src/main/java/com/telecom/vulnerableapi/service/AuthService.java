package com.telecom.vulnerableapi.service;

import com.telecom.vulnerableapi.dto.LoginRequest;
import com.telecom.vulnerableapi.dto.LoginResponse;
import com.telecom.vulnerableapi.model.AppUser;
import com.telecom.vulnerableapi.repository.UserRepository;
import com.telecom.vulnerableapi.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        // VULNERABILITY: Missing input sanitization/validation for username and password.
        AppUser user = userRepository.findByUsernameUnsafe(request.getUsername());
        if (user == null || !Objects.equals(user.getPassword(), request.getPassword())) {
            return new LoginResponse(null, "Invalid username or password");
        }

        // VULNERABILITY: Passwords are stored and compared in plaintext.
        String token = jwtUtil.issueToken(user);
        return new LoginResponse(token, "Login successful");
    }
}

