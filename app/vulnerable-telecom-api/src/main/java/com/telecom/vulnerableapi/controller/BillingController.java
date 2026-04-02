package com.telecom.vulnerableapi.controller;

import com.telecom.vulnerableapi.model.BillingAccount;
import com.telecom.vulnerableapi.security.JwtUtil;
import com.telecom.vulnerableapi.security.RequesterContext;
import com.telecom.vulnerableapi.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final JwtUtil jwtUtil;

    public BillingController(BillingService billingService, JwtUtil jwtUtil) {
        this.billingService = billingService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BillingAccount> getBillingByAccountId(
            @PathVariable("accountId") String accountId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        RequesterContext requester = jwtUtil.validateAndExtract(authorizationHeader);
        BillingAccount billingAccount = billingService.getBillingByAccountId(accountId, requester);
        if (billingAccount == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(billingAccount);
    }
}

