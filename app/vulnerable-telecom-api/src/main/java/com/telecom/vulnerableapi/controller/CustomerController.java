package com.telecom.vulnerableapi.controller;

import com.telecom.vulnerableapi.model.Customer;
import com.telecom.vulnerableapi.security.JwtUtil;
import com.telecom.vulnerableapi.security.RequesterContext;
import com.telecom.vulnerableapi.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    public CustomerController(CustomerService customerService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable("id") String id,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        RequesterContext requester = jwtUtil.validateAndExtract(authorizationHeader);
        Customer customer = customerService.getCustomerById(id, requester);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }
}

