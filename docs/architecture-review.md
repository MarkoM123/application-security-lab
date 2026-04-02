# Architecture Review Notes (Security-Focused)

## Application overview

The target is a monolithic Spring Boot REST API representing a telecom backend.  
The application exposes authentication, customer profile access, billing lookup, ticket operations, admin listing, and file upload features. Data is stored in an H2 database, and uploaded files are stored on local disk.

## Security-sensitive components

- `security/JwtUtil`: token issuance and trust decisions for caller identity
- `controller/*`: public HTTP attack surface and request entry points
- `service/*`: authorization and business-logic controls
- `repository/*`: SQL construction and data access patterns
- `service/FileStorageService`: untrusted file handling and storage path decisions
- `resources/application.yml`: sensitive configuration and secret exposure risk

## Trust assumptions observed

- Request headers are assumed to carry valid identity claims.
- Path IDs are assumed safe to use in data access.
- User input in tickets is assumed safe to persist and return.
- Uploaded file metadata is assumed trustworthy.
- Source-controlled secrets are implicitly treated as acceptable for runtime.

These assumptions are not safe in production and drive several high-risk findings.

## Attack surface review

- Authentication endpoint accepts unvalidated credentials and returns weakly protected token material.
- Data access endpoints (`/customers/{id}`, `/billing/{accountId}`) expose object-level authorization gaps.
- Admin endpoint (`/admin/users`) lacks role enforcement.
- Repository queries include string concatenation with untrusted input.
- Ticket workflow stores and returns raw user input.
- File upload endpoint accepts content with minimal validation and unsanitized naming.

## Security observations

- Authorization logic is inconsistent across sensitive flows.
- Identity validation is not cryptographically robust.
- Input handling controls are incomplete across login, ticketing, and file upload.
- Configuration hygiene is weak (hardcoded secrets in repository).
- Defensive controls are not centralized, making drift likely as code evolves.

## Key architectural weaknesses

1. Authorization checks are not enforced as a systemic pattern.
2. Token validation does not provide strong authentication guarantees.
3. Data-access layer permits injection-prone query construction.
4. Untrusted content flows (text and files) are not constrained at boundaries.
5. Secret management is embedded in app config rather than environment or vault controls.

## Security improvement opportunities

- Introduce centralized authentication and authorization policy enforcement.
- Replace custom weak token handling with standard JWT verification and strict claims policy.
- Standardize parameterized query usage in all repository operations.
- Enforce request DTO validation and output encoding strategy.
- Implement secure file upload policy (allowlist, size caps, random names, storage segmentation).
- Move secrets to environment-backed secret management and rotate exposed values.
