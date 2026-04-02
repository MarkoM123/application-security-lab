package com.telecom.vulnerableapi.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.vulnerableapi.model.AppUser;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String issueToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("exp", System.currentTimeMillis() + 3_600_000L);

        try {
            String json = objectMapper.writeValueAsString(claims);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to issue token", ex);
        }
    }

    public RequesterContext validateAndExtract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return RequesterContext.anonymous();
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            // VULNERABILITY: This is not a real JWT validation flow.
            // The token is treated as plain Base64 JSON and signature verification is missing.
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            Map<String, Object> claims = objectMapper.readValue(decoded, new TypeReference<Map<String, Object>>() {
            });

            // VULNERABILITY: "exp" claim is present but not enforced.
            Object userIdRaw = claims.get("userId");
            Long userId = userIdRaw instanceof Number ? ((Number) userIdRaw).longValue() : -1L;
            String role = String.valueOf(claims.getOrDefault("role", "USER"));
            return new RequesterContext(userId, role, token);
        } catch (Exception ex) {
            // VULNERABILITY: Invalid/tampered token falls back to anonymous access.
            return RequesterContext.anonymous();
        }
    }
}

