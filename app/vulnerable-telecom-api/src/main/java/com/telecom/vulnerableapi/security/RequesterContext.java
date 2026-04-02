package com.telecom.vulnerableapi.security;

public class RequesterContext {

    private Long userId;
    private String role;
    private String rawToken;

    public RequesterContext() {
    }

    public RequesterContext(Long userId, String role, String rawToken) {
        this.userId = userId;
        this.role = role;
        this.rawToken = rawToken;
    }

    public static RequesterContext anonymous() {
        return new RequesterContext(-1L, "ANONYMOUS", null);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRawToken() {
        return rawToken;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }
}

