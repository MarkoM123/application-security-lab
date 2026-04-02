package com.telecom.vulnerableapi.repository;

import com.telecom.vulnerableapi.model.BillingAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BillingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BillingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BillingAccount findByAccountIdUnsafe(String accountId) {
        String sql = "SELECT account_id, customer_id, balance, plan_name, owner_user_id FROM billing_account WHERE account_id = " + accountId;
        List<BillingAccount> accounts = jdbcTemplate.query(sql, billingRowMapper());
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    private RowMapper<BillingAccount> billingRowMapper() {
        return (rs, rowNum) -> {
            BillingAccount account = new BillingAccount();
            account.setAccountId(rs.getLong("account_id"));
            account.setCustomerId(rs.getLong("customer_id"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setPlanName(rs.getString("plan_name"));
            account.setOwnerUserId(rs.getLong("owner_user_id"));
            return account;
        };
    }
}

