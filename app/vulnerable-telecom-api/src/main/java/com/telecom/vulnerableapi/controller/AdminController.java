package com.telecom.vulnerableapi.controller;

import com.telecom.vulnerableapi.model.AppUser;
import com.telecom.vulnerableapi.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAdminUsers(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // VULNERABILITY: No authentication or role check.
        // Any caller can access admin user data without ADMIN authorization.
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}

