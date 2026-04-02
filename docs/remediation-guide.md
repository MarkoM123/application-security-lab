# Remediation Guide

This guide is written for developers who will harden the API while keeping behavior stable.  
Each section describes the required change, why it matters, and common implementation pitfalls observed in Java API projects.

## 1. Customer object access control (`/customers/{id}`)

### What should change

- Add object ownership checks before returning customer records.
- Return `403 Forbidden` when a user requests an object they do not own.

### Why it matters

Without object-level checks, any authenticated user can read another customer's data.

### Secure implementation principle

`deny by default` and enforce authorization at every object retrieval point.

### Implementation guidance

- Compare `customer.ownerUserId` against `requester.userId`.
- Allow privileged override only for explicit admin roles.
- Keep this check in service logic, not only in controller code.

### Developer notes / common pitfalls

- Do not assume numeric ID format means safe access.
- Do not rely on frontend filtering as a security boundary.

## 2. Billing object access control (`/billing/{accountId}`)

### What should change

- Apply ownership and role authorization checks for billing account lookup.
- Ensure cross-account requests are denied consistently.

### Why it matters

Billing data is sensitive and high-value; cross-account exposure is both a privacy and trust issue.

### Secure implementation principle

Authorize per object, not just per endpoint.

### Implementation guidance

- Load account and compare `ownerUserId` with caller identity.
- Use consistent authorization helpers shared across customer and billing services.

### Developer notes / common pitfalls

- Avoid duplicate authorization logic that diverges over time.
- Include negative tests (user A cannot access user B account).

## 3. Admin function authorization (`/admin/users`)

### What should change

- Require authenticated admin role for this route.
- Add framework-level and service-level checks.

### Why it matters

Function-level authorization gaps expose privileged operations to unauthorized users.

### Secure implementation principle

Apply least privilege and defense in depth for administrative paths.

### Implementation guidance

- Add route guard annotations (for example `@PreAuthorize("hasRole('ADMIN')")`).
- Validate role membership from verified token claims only.

### Developer notes / common pitfalls

- Do not trust "admin" role from unverified token payloads.
- Avoid implementing admin checks only in UI workflows.

## 4. SQL injection in repository queries

### What should change

- Replace SQL string concatenation with parameterized queries.
- Validate and coerce numeric inputs before data access.

### Why it matters

Concatenated query construction can allow untrusted input to alter SQL behavior.

### Secure implementation principle

Treat all external input as untrusted and keep SQL structure static.

### Implementation guidance

- Use `JdbcTemplate` with placeholders (`?`) and bound parameters.
- Migrate query methods in `UserRepository`, `CustomerRepository`, and `BillingRepository`.

### Developer notes / common pitfalls

- Parameterization does not replace business authorization checks.
- Avoid partial fixes where one repository method remains unsafe.

## 5. Ticket content handling (stored/reflected XSS exposure)

### What should change

- Validate ticket fields for acceptable length and character set.
- Apply context-aware output encoding in any UI that renders ticket data.

### Why it matters

Untrusted text can become executable content in downstream clients if rendered unsafely.

### Secure implementation principle

Validate on input, encode on output, and avoid trust transfer across system boundaries.

### Implementation guidance

- Add DTO constraints (`@NotBlank`, `@Size`) and optional sanitization policy.
- Document rendering requirements for any consumer application.

### Developer notes / common pitfalls

- Sanitization alone is not enough; output encoding still matters.
- Do not strip characters aggressively without product input (support workflows may need symbols).

## 6. Hardcoded secrets in configuration

### What should change

- Remove plaintext secrets from `application.yml`.
- Load secrets from environment variables or managed secret store.
- Rotate values that were committed.

### Why it matters

Source-controlled secrets are easy to leak and difficult to track safely over time.

### Secure implementation principle

Separate code from secrets and enforce short credential exposure windows.

### Implementation guidance

- Replace static values with `${ENV_VAR}` placeholders.
- Add secret scanning in CI and prevent future committed secrets.

### Developer notes / common pitfalls

- Removing secrets from latest commit is not enough; rotate values.
- Avoid reusing one secret across environments.

## 7. File upload hardening (`/files/upload`)

### What should change

- Enforce file type and extension allowlists.
- Enforce size limits and reject empty uploads.
- Replace user filename with randomized server-generated filename.

### Why it matters

File upload is a high-risk boundary where untrusted binary content enters the system.

### Secure implementation principle

Minimize trust in client-provided metadata and isolate upload storage.

### Implementation guidance

- Validate `contentType`, extension, and maximum file size.
- Normalize destination path and store outside directly served paths.
- Log upload metadata for auditability.

### Developer notes / common pitfalls

- MIME type checks alone are bypassable; combine with extension and content checks where possible.
- Avoid returning internal absolute storage paths in API responses.

## 8. Weak JWT validation

### What should change

- Replace custom Base64 parsing with standards-based JWT verification.
- Enforce signature checks, token expiry, and expected claims.

### Why it matters

If token trust is weak, all authorization decisions become unreliable.

### Secure implementation principle

Only trust identity claims after cryptographic verification.

### Implementation guidance

- Use a maintained JWT library and pin accepted algorithms.
- Reject invalid/expired tokens with clear `401` responses.
- Avoid silent fallback to anonymous context for protected endpoints.

### Developer notes / common pitfalls

- Decoding is not verification.
- Keep signing keys outside application code and rotate periodically.

## 9. Baseline input validation across auth and ticket flows

### What should change

- Introduce bean validation annotations on request DTOs.
- Add centralized validation error handling for consistent API responses.

### Why it matters

Validation gaps increase misuse risk and make downstream logic harder to secure.

### Secure implementation principle

Validate early at API boundaries, then enforce domain-specific rules in services.

### Implementation guidance

- Add `@Valid` on controller methods and constraints in DTO classes.
- Define explicit max lengths and accepted formats for each field.

### Developer notes / common pitfalls

- Avoid generic `String` acceptance without constraints.
- Keep validation messages developer-friendly but not overly verbose for attackers.
