# Validation Evidence: Weak JWT Validation

## Endpoint reviewed

- Token validation flow in `JwtUtil`, consumed by protected endpoint handlers

## What was tested conceptually

- Whether token claims are cryptographically verified before being trusted.
- Whether expiration and integrity checks are enforced.
- Whether invalid token handling fails closed.

## Observed insecure behavior

- Token parsing is implemented as Base64 decoding of claim JSON.
- Signature verification is absent.
- Expiration claim is present but not strictly enforced.
- Error handling can degrade to anonymous context rather than strict token rejection.

## Why the finding was considered valid

- The code path does not perform cryptographic verification and still derives requester role/ID from token content.
- This directly undermines trust in authentication and authorization decisions.
