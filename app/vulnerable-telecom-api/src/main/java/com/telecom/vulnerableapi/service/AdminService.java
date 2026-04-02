package com.telecom.vulnerableapi.service;

import com.telecom.vulnerableapi.model.AppUser;
import com.telecom.vulnerableapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAllUsers();
    }
}

