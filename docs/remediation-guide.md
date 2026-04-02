# Remediation Guide

## Purpose
This guide describes how to fix each intentionally introduced vulnerability in `vulnerable-telecom-api` and align the codebase with secure Java backend practices.

## 1. Fix IDOR in `/customers/{id}` and `/billing/{accountId}`

### What to change
- Derive caller identity from a validated token.
- Enforce object-level authorization before returning records.
- Return `403 Forbidden` on ownership mismatch.

### Java example
```java
if (!record.getOwnerUserId().equals(requester.getUserId()) && !"ADMIN".equals(requester.getRole())) {
    throw new AccessDeniedException("Forbidden");
}
```

### Best practices
- Implement authorization centrally (service policy or authorization middleware).
- Add unit and integration tests for cross-account access attempts.

## 2. Fix Broken Admin Authorization

### What to change
- Require authenticated identity for admin routes.
- Enforce role checks (`ADMIN`) using Spring Security annotations and HTTP config.

### Java example
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public List<AppUser> listUsers() { ... }
```

### Best practices
- Apply least privilege.
- Use defense in depth: route guards plus service-layer checks.

## 3. Fix SQL Injection

### What to change
- Replace string-concatenated SQL with prepared statements / parameterized queries.
- Validate numeric path parameters before DB access.

### Java example
```java
String sql = "SELECT * FROM customer WHERE id = ?";
Customer c = jdbcTemplate.queryForObject(sql, customerMapper, customerId);
```

### Best practices
- Never interpolate untrusted input into SQL.
- Favor ORM query binding where possible.

## 4. Fix XSS in Ticket Data

### What to change
- Validate and constrain input length and allowed characters.
- Encode output in the rendering context (HTML, JS, URL).
- Optionally sanitize rich-text input with a strict allowlist policy.

### Java example
```java
String safeSubject = HtmlUtils.htmlEscape(request.getSubject());
ticket.setSubject(safeSubject);
```

### Best practices
- Treat all client input as untrusted.
- Do not rely on client-side sanitization only.

## 5. Remove Hardcoded Secrets

### What to change
- Move secrets to environment variables or secret manager.
- Rotate all exposed secrets.

### Java example
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
app:
  jwt:
    secret: ${APP_JWT_SECRET}
```

### Best practices
- Enforce secret scanning in CI.
- Separate secrets by environment (dev/stage/prod).

## 6. Secure File Upload

### What to change
- Enforce file type and extension allowlist.
- Enforce max file size.
- Replace user-supplied file name with server-generated name.
- Store uploads outside web root and scan uploads when applicable.

### Java example
```java
if (!allowedTypes.contains(file.getContentType()) || file.getSize() > MAX_BYTES) {
    throw new IllegalArgumentException("Invalid file");
}
String storedName = UUID.randomUUID() + ".bin";
```

### Best practices
- Block executable or scriptable file types unless explicitly required.
- Log upload metadata for monitoring and incident response.

## 7. Fix Weak JWT Validation

### What to change
- Use a standard JWT library with signature verification.
- Validate `exp`, `iat`, `iss`, `aud` and algorithm policy.
- Reject invalid tokens with `401 Unauthorized`.

### Java example
```java
DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
    .withIssuer("telecom-api")
    .build()
    .verify(token);
```

### Best practices
- Keep signing keys in secret manager.
- Use short token lifetime and refresh tokens where appropriate.

## 8. Add Input Validation and Sanitization Baseline

### What to change
- Use Bean Validation (`@NotBlank`, `@Size`, `@Email`, etc.) on DTOs.
- Add centralized exception handling for validation failures.

### Java example
```java
public class LoginRequest {
    @NotBlank
    @Size(max = 64)
    private String username;
}
```

### Best practices
- Validate at API boundary and again for domain-specific rules.
- Define explicit constraints for every external input field.

## Secure Coding Checklist (Java Backend)
- Use parameterized queries only.
- Enforce authentication and authorization on every sensitive route.
- Protect secrets with managed secret storage.
- Validate all input and encode all output.
- Harden file upload pipelines.
- Add logging, monitoring, and alerting for authentication and data-access anomalies.
- Integrate SAST/DAST into CI/CD with build-breaking thresholds for high-risk findings.

