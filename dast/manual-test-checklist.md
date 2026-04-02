# Manual Test Checklist (Defensive)

## Pre-checks

- [ ] API starts cleanly in local environment.
- [ ] Seed data is loaded (`alice`, `bob`, `admin` users).
- [ ] Base URL is reachable (`http://localhost:8080`).

## Authentication

- [ ] Confirm valid credentials receive a token.
- [ ] Confirm invalid credentials are denied consistently.
- [ ] Confirm invalid/tampered/expired tokens are rejected on protected flows.
- [ ] Confirm protected endpoints do not silently downgrade to anonymous access.

## Authorization

- [ ] Verify `/admin/users` is restricted to admin role only.
- [ ] Verify non-admin authenticated users cannot access admin function.
- [ ] Verify anonymous callers cannot access privileged routes.

## Object access

- [ ] Verify user cannot access another customer's `/customers/{id}` record.
- [ ] Verify user cannot access another account's `/billing/{accountId}` record.
- [ ] Verify object-level checks apply even with valid authentication.

## Input handling

- [ ] Validate boundary behavior for missing/blank/oversized fields.
- [ ] Validate numeric fields reject non-numeric values where required.
- [ ] Validate API returns controlled validation errors for malformed input.

## Stored content

- [ ] Submit ticket content that includes markup-like input.
- [ ] Verify stored data handling is safe for downstream rendering contexts.
- [ ] Confirm data is constrained by field length and validation rules.

## File upload

- [ ] Verify unsupported file types are rejected.
- [ ] Verify oversized uploads are rejected.
- [ ] Verify server-generated file naming is used.
- [ ] Verify upload response does not expose sensitive filesystem paths.

## Error handling

- [ ] Verify authentication failures return appropriate `401/403` responses.
- [ ] Verify server errors do not expose stack traces or sensitive internals.
- [ ] Verify rejection responses are consistent and actionable for clients.

## API misuse

- [ ] Verify repeated access attempts do not expose additional data.
- [ ] Verify endpoint behavior is consistent under unusual call order.
- [ ] Verify privileged APIs are not reachable through alternate paths.
