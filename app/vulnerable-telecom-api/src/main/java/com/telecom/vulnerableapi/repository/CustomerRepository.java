package com.telecom.vulnerableapi.repository;

import com.telecom.vulnerableapi.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer findByIdUnsafe(String customerId) {
        // VULNERABILITY: SQL Injection risk due to direct string concatenation in SQL query.
        String sql = "SELECT id, full_name, email, phone_number, account_id, owner_user_id FROM customer WHERE id = " + customerId;
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper());
        return customers.isEmpty() ? null : customers.get(0);
    }

    private RowMapper<Customer> customerRowMapper() {
        return (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setFullName(rs.getString("full_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhoneNumber(rs.getString("phone_number"));
            customer.setAccountId(rs.getLong("account_id"));
            customer.setOwnerUserId(rs.getLong("owner_user_id"));
            return customer;
        };
    }
}

