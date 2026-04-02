# Validation Evidence: Customer IDOR

## Endpoint reviewed

- `GET /customers/{id}`

## What was tested conceptually

- Whether a non-owner authenticated user can request another customer's record by changing the object identifier.
- Whether service logic enforces ownership (`ownerUserId`) before returning data.

## Observed insecure behavior

- The request path ID controls record selection.
- No object-level authorization check is applied before the response is returned.
- Cross-user customer data can be read when the identifier is known.

## Why the finding was considered valid

- Source review and runtime behavior were aligned: `RequesterContext` is available but not enforced in `CustomerService`.
- The endpoint behavior demonstrates broken object-level authorization, which is sufficient evidence for IDOR risk.
