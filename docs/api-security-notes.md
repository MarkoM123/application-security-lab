# API Security Notes (OWASP API Security Top 10 2023 Mapping)

This lab maps primarily to the following OWASP API Security Top 10 (2023) categories.

## API1:2023 Broken Object Level Authorization

Why it fits:

- `GET /customers/{id}` returns records without ownership verification.
- `GET /billing/{accountId}` allows cross-account data access when identifiers are manipulated.

## API2:2023 Broken Authentication

Why it fits:

- Token handling relies on Base64 decode and does not enforce signature integrity.
- Expiration claims are present but not enforced, weakening trust in caller identity.

## API5:2023 Broken Function Level Authorization

Why it fits:

- `GET /admin/users` is reachable without an admin role check.
- Privileged function exposure is not restricted to intended roles.

## API8:2023 Security Misconfiguration

Why it fits:

- Hardcoded secrets are committed in configuration.
- File upload protections are missing or incomplete.
- Security defaults and operational guardrails are not consistently applied.

## Related categories also touched

- `API3:2023 Broken Object Property Level Authorization`: overexposure of user data on admin listing.
- `API10:2023 Unsafe Consumption of APIs`: weak trust handling patterns that could cascade into downstream misuse.
