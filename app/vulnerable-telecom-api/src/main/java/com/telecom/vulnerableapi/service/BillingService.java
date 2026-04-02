package com.telecom.vulnerableapi.service;

import com.telecom.vulnerableapi.model.BillingAccount;
import com.telecom.vulnerableapi.repository.BillingRepository;
import com.telecom.vulnerableapi.security.RequesterContext;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public BillingAccount getBillingByAccountId(String accountId, RequesterContext requester) {
        BillingAccount account = billingRepository.findByAccountIdUnsafe(accountId);

        // VULNERABILITY: IDOR - no check that requester owns the requested account.
        return account;
    }
}

