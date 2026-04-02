# Java Secure Code Review Findings

This file documents insecure code samples from the project and secure alternatives suitable for production code.

## 1. SQL Injection in Repository Query

### Vulnerable snippet
```java
String sql = "SELECT id, username, password, role FROM app_user WHERE username = '" + username + "'";
List<AppUser> users = jdbcTemplate.query(sql, userRowMapper());
```

### Why vulnerable
- User-controlled input is concatenated into SQL.
- Query semantics can be altered by crafted input.

### Secure version
```java
String sql = "SELECT id, username, password, role FROM app_user WHERE username = ?";
List<AppUser> users = jdbcTemplate.query(sql, userRowMapper(), username);
```

## 2. IDOR in Customer Access

### Vulnerable snippet
```java
public Customer getCustomerById(String customerId, RequesterContext requester) {
    Customer customer = customerRepository.findByIdUnsafe(customerId);
    return customer;
}
```

### Why vulnerable
- Resource ownership is never checked.
- Any authenticated user can request any record by identifier.

### Secure version
```java
public Customer getCustomerById(String customerId, RequesterContext requester) {
    Customer customer = customerRepository.findById(customerId);
    if (customer == null) {
        return null;
    }
    if (!customer.getOwnerUserId().equals(requester.getUserId()) && !"ADMIN".equals(requester.getRole())) {
        throw new AccessDeniedException("Forbidden");
    }
    return customer;
}
```

## 3. Broken Authorization on Admin Endpoint

### Vulnerable snippet
```java
@GetMapping("/users")
public ResponseEntity<List<AppUser>> getAdminUsers(...) {
    return ResponseEntity.ok(adminService.getAllUsers());
}
```

### Why vulnerable
- Endpoint exposes admin data without role verification.
- No defense in depth at controller/service level.

### Secure version
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/users")
public ResponseEntity<List<AppUser>> getAdminUsers() {
    return ResponseEntity.ok(adminService.getAllUsers());
}
```

## 4. Weak JWT Validation

### Vulnerable snippet
```java
String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
Map<String, Object> claims = objectMapper.readValue(decoded, new TypeReference<>() {});
return new RequesterContext(userId, role, token);
```

### Why vulnerable
- Base64 decoding is not JWT signature verification.
- Expiration and issuer claims are not enforced.

### Secure version
```java
DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
    .withIssuer("telecom-api")
    .build()
    .verify(token);
Long userId = jwt.getClaim("userId").asLong();
String role = jwt.getClaim("role").asString();
```

## 5. XSS Risk in Ticket Creation

### Vulnerable snippet
```java
ticket.setSubject(request.getSubject());
ticket.setMessage(request.getMessage());
```

### Why vulnerable
- Untrusted data is stored and returned without validation or encoding.
- Downstream UI may render unsafe content.

### Secure version
```java
String sanitizedSubject = HtmlUtils.htmlEscape(request.getSubject());
String sanitizedMessage = HtmlUtils.htmlEscape(request.getMessage());
ticket.setSubject(sanitizedSubject);
ticket.setMessage(sanitizedMessage);
```

## 6. Hardcoded Secrets in Configuration

### Vulnerable snippet
```yaml
app:
  jwt:
    secret: hardcoded-very-weak-demo-secret
```

### Why vulnerable
- Secrets in source control are accessible to all code readers.
- Rotation and environment separation become difficult.

### Secure version
```yaml
app:
  jwt:
    secret: ${APP_JWT_SECRET}
```

## 7. Insecure File Upload

### Vulnerable snippet
```java
Path destination = uploadDir.resolve(originalFileName);
Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
```

### Why vulnerable
- Missing allowlist validation (type, extension, size).
- User-controlled filename directly impacts storage path.

### Secure version
```java
String safeName = UUID.randomUUID() + ".pdf";
if (!allowedContentTypes.contains(file.getContentType()) || file.getSize() > maxSizeBytes) {
    throw new IllegalArgumentException("Invalid upload");
}
Path destination = uploadDir.resolve(safeName).normalize();
Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
```

