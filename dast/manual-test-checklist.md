# Manual DAST Checklist

## Pre-Checks
- Application starts successfully.
- Sample users from `data.sql` are loaded.
- Base URL is reachable: `http://localhost:8080`.

## Authentication
- [ ] `POST /login` returns token for valid credentials.
- [ ] Invalid credentials return rejection response.
- [ ] Observe whether expired/tampered token is rejected.

## Authorization
- [ ] Access `/admin/users` without token.
- [ ] Access `/admin/users` with non-admin token.
- [ ] Confirm route is properly denied after remediation.

## IDOR
- [ ] Authenticate as User A and request User B's `/customers/{id}`.
- [ ] Authenticate as User A and request User B's `/billing/{accountId}`.
- [ ] Confirm cross-account requests are blocked after remediation.

## Input Handling
- [ ] Submit malformed/oversized values to `/login`.
- [ ] Submit markup-like strings in `/tickets`.
- [ ] Verify output handling in any consumer UI/API response.

## File Upload
- [ ] Upload unsupported extension/type files.
- [ ] Upload oversized file.
- [ ] Verify file path and naming policy after remediation.

## Reporting
- [ ] Capture request/response evidence per finding.
- [ ] Assign severity and business impact.
- [ ] Link each finding to code location and remediation task.

