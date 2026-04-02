package com.telecom.vulnerableapi.repository;

import com.telecom.vulnerableapi.model.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AppUser findByUsernameUnsafe(String username) {
        // VULNERABILITY: SQL Injection via string concatenation.
        String sql = "SELECT id, username, password, role FROM app_user WHERE username = '" + username + "'";
        List<AppUser> users = jdbcTemplate.query(sql, userRowMapper());
        return users.isEmpty() ? null : users.get(0);
    }

    public List<AppUser> findAllUsers() {
        String sql = "SELECT id, username, password, role FROM app_user";
        return jdbcTemplate.query(sql, userRowMapper());
    }

    private RowMapper<AppUser> userRowMapper() {
        return (rs, rowNum) -> {
            AppUser user = new AppUser();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        };
    }
}

