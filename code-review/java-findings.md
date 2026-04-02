# Java Source Review Findings

This document captures source-level review notes for common insecure patterns found in the lab API.  
It is written as an internal AppSec handoff for development teams.

## 1. SQL query concatenation in repositories

- Vulnerable code pattern summary: SQL strings are assembled with user-controlled values (`username`, `id`, `accountId`) before execution with `JdbcTemplate`.
- Why it is risky: Query semantics can be altered, enabling unauthorized data retrieval and bypass of intended constraints.
- What to look for during review:
  - SQL strings containing `+` with request/path inputs
  - Repository methods receiving raw `String` IDs for numeric fields
  - Shared helper methods that build SQL dynamically without bind parameters
- Secure rewrite recommendation:
  - Use parameterized SQL with placeholders and typed binding.
  - Validate input types at controller/service boundary before repository calls.
- Reviewer note: Fix this class of issue centrally and sweep all repositories in one pass to avoid partial remediation.

## 2. Missing object authorization in customer service

- Vulnerable code pattern summary: Service fetches `Customer` by ID and returns it directly without checking owner-to-requester mapping.
- Why it is risky: Any authenticated user can read another customer's data by changing object identifiers.
- What to look for during review:
  - Methods that accept both `resourceId` and `RequesterContext` but never compare them
  - Controllers that pass auth context into services but do not enforce decisions
- Secure rewrite recommendation:
  - Enforce `ownerUserId == requester.userId` with explicit admin override.
  - Return `403` for mismatch and add dedicated access tests.
- Reviewer note: This is a policy issue as much as a code issue; define object access rules once and reuse.

## 3. Missing object authorization in billing service

- Vulnerable code pattern summary: Billing account lookup returns data without ownership or role checks.
- Why it is risky: Exposes account plan and balance information across tenants/users.
- What to look for during review:
  - Data-returning methods with no authorization gate
  - Inconsistent checks between customer and billing flows
- Secure rewrite recommendation:
  - Add reusable authorization helper for owner/admin checks.
  - Ensure all account and customer reads use the same access policy.
- Reviewer note: Keep policy decisions in service layer, not in repository.

## 4. Admin endpoint without function-level guard

- Vulnerable code pattern summary: `/admin/users` controller path returns privileged data with no role requirement.
- Why it is risky: Non-admin users can invoke admin functionality directly.
- What to look for during review:
  - Controllers under `/admin` lacking `@PreAuthorize` or equivalent
  - Service methods returning admin data without caller-role verification
- Secure rewrite recommendation:
  - Add route-level admin authorization and service-level role assertions.
  - Reject unauthorized requests with `403`.
- Reviewer note: Treat admin routes as high-risk and include them in mandatory security tests.

## 5. Weak token validation flow in `JwtUtil`

- Vulnerable code pattern summary: Token is Base64-decoded as JSON claims; no signature verification; expiry not strictly enforced.
- Why it is risky: Claims can be tampered with and still trusted by downstream authorization logic.
- What to look for during review:
  - Custom JWT parsing with `Base64.getDecoder()` only
  - Missing checks for `exp`, issuer, audience, and accepted algorithms
  - Anonymous fallback behavior on token parsing errors
- Secure rewrite recommendation:
  - Use verified JWT library flow (`verify`) with strict claim checks.
  - Fail closed for invalid tokens on protected routes.
- Reviewer note: Authentication integrity must be fixed before relying on any role-based policy.

## 6. Ticket handling accepts untrusted content as-is

- Vulnerable code pattern summary: `subject` and `message` are persisted and returned without constraints or encoding strategy.
- Why it is risky: Creates stored/reflected XSS exposure in downstream rendering clients.
- What to look for during review:
  - DTO fields without length/content constraints
  - Raw pass-through from request to persistence model
  - API responses returning user text intended for UI rendering
- Secure rewrite recommendation:
  - Validate field size/content at DTO boundary.
  - Encode in output context and optionally sanitize where business-appropriate.
- Reviewer note: Pair backend controls with frontend safe rendering standards.

## 7. Hardcoded secrets in configuration

- Vulnerable code pattern summary: JWT secret, DB password, and API key are stored in plaintext in `application.yml`.
- Why it is risky: Repository exposure or artifact leakage can reveal live credentials and token signing material.
- What to look for during review:
  - Config keys like `password`, `secret`, `api-key` with literal values
  - Environment values checked into local defaults
- Secure rewrite recommendation:
  - Use environment-backed placeholders and secret management.
  - Rotate existing values and document rotation ownership.
- Reviewer note: This is both a code hygiene and operational process fix.

## 8. Upload storage trusts client filename and metadata

- Vulnerable code pattern summary: Original filename is used in storage path and no allowlist/size validation is applied.
- Why it is risky: Allows unsafe content entry and increases path-related misuse risk.
- What to look for during review:
  - Direct use of `getOriginalFilename()` in file path construction
  - Missing size checks and content-type checks
  - API response exposing internal filesystem path
- Secure rewrite recommendation:
  - Validate file type and size.
  - Generate server-side filename and isolate upload storage.
- Reviewer note: File upload controls should be treated as a dedicated security control set, not ad hoc validation.

## 9. Missing baseline request validation

- Vulnerable code pattern summary: Login and ticket DTOs accept unbounded strings without bean validation.
- Why it is risky: Malformed/unexpected input reaches core logic and broadens attack surface.
- What to look for during review:
  - Controllers accepting DTOs without `@Valid`
  - DTO classes without `@NotBlank`, `@Size`, or `@Pattern`
- Secure rewrite recommendation:
  - Add validation annotations and centralized validation error handling.
  - Define field constraints aligned with business requirements.
- Reviewer note: Validation is foundational and should be standardized across all endpoints.
