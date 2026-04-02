# Threat Model (Lightweight)

## Scope

In-scope components:

- Java Spring Boot REST API in `app/vulnerable-telecom-api`
- Authentication and token handling (`/login`, JWT utility)
- Customer and billing data access endpoints
- Ticket submission and listing endpoints
- Admin user listing endpoint
- File upload endpoint

Out of scope for this lab:

- Infrastructure hardening beyond local runtime
- Network-layer controls (WAF, reverse proxy policies)
- Third-party integrations and production IAM

## Protected assets

- Customer profile data (name, email, phone)
- Billing account data (balances, plan details)
- User account data and role information
- Authentication tokens and signing material
- Uploaded files and file metadata
- Operational trust in admin-only functionality

## User roles

- `USER`: standard customer-level access to own records
- `ADMIN`: privileged operational access
- `ANONYMOUS`: unauthenticated caller (should be limited)

## Trust boundaries

- External client to API boundary (`HTTP` request entry)
- Token parsing/validation boundary (claim trust decision)
- Service authorization boundary (access control checks)
- Repository/database boundary (query safety and data exposure)
- File upload boundary (untrusted binary content to server storage)

## Entry points

- `POST /login`
- `GET /customers/{id}`
- `GET /billing/{accountId}`
- `POST /tickets`
- `GET /tickets`
- `GET /admin/users`
- `POST /files/upload`

## Sensitive business flows

- Authentication and token issuance
- Access to customer and billing records
- Administrative visibility into user inventory
- Ticket submission and later retrieval by support workflows
- File intake and storage for operational cases

## Abuse cases

- A regular user requests another customer's record by changing identifiers.
- A non-admin caller directly reaches admin APIs.
- Untrusted input alters SQL behavior when concatenated into queries.
- Unsanitized ticket content is stored and later rendered in a UI context.
- Token claims are modified and accepted due to weak validation.
- Dangerous or untrusted files are uploaded without restriction.

## Top security concerns

1. Broken object-level and function-level authorization.
2. Broken authentication integrity due to weak token validation.
3. Injection and untrusted input handling weaknesses.
4. Security misconfiguration (hardcoded secrets, weak upload controls).
5. Data exposure risk across customer, billing, and admin workflows.
