package com.telecom.vulnerableapi.service;

import com.telecom.vulnerableapi.model.Customer;
import com.telecom.vulnerableapi.repository.CustomerRepository;
import com.telecom.vulnerableapi.security.RequesterContext;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(String customerId, RequesterContext requester) {
        Customer customer = customerRepository.findByIdUnsafe(customerId);

        // VULNERABILITY: IDOR - requester identity is available but there is no ownership check.
        // Expected check example: customer.getOwnerUserId().equals(requester.getUserId())
        return customer;
    }
}

