# Security Findings Report

## Assessment Context

- Project: `application-security-review-lab`
- Target: `app/vulnerable-telecom-api`
- Review date: 2026-04-02
- Review type: manual code-centric AppSec assessment with SAST/DAST-assisted validation
- Scope: auth, customer and billing access control, admin functionality, ticketing, upload handling, configuration

## Risk Rating Approach

Findings were rated using practical impact and exploitation likelihood in this API context:

- `Critical`: control failure that can directly break authentication trust, admin boundaries, or core data protection
- `High`: clear unauthorized access or material data/security exposure with realistic abuse potential
- `Medium`: weakness that is meaningful on its own and can amplify other issues
- `Low`: defense-in-depth gap with limited direct impact

Business impact was prioritized around customer data exposure, billing data exposure, and compromise of privileged operations.

## F-01

- Title: Customer endpoint allows unauthorized record access (IDOR)
- Severity: High
- CWE: CWE-639 (Authorization Bypass Through User-Controlled Key)
- OWASP API Security Top 10 2023 Mapping: API1:2023 Broken Object Level Authorization
- Affected Component: `CustomerService`, `CustomerController`, `/customers/{id}`
- Description: The endpoint accepts a customer ID from the path and returns the record without verifying that the requester owns that object.
- Technical Root Cause: Authorization context is available (`RequesterContext`) but never used for object ownership enforcement before data return.
- Security Impact: Any authenticated user can access another user's profile data by changing identifiers.
- Business Impact: Unauthorized disclosure of customer PII can create compliance, privacy, and trust issues.
- Validation Notes: Review of service logic confirmed no ownership check (`ownerUserId` vs requester ID). Local behavior matched unrestricted record access when identifiers changed.
- Recommended Remediation: Enforce object-level authorization in service logic and return `403` for ownership mismatch unless an approved privileged role is present.

## F-02

- Title: Billing endpoint exposes cross-account billing data (IDOR)
- Severity: High
- CWE: CWE-639 (Authorization Bypass Through User-Controlled Key)
- OWASP API Security Top 10 2023 Mapping: API1:2023 Broken Object Level Authorization
- Affected Component: `BillingService`, `BillingController`, `/billing/{accountId}`
- Description: Billing records are returned by account ID without validating ownership or policy-based access rights.
- Technical Root Cause: Repository results are returned directly with no authorization decision on `ownerUserId`.
- Security Impact: Users can retrieve other customers' billing plan and balance information.
- Business Impact: Financial data exposure increases regulatory risk and can damage customer trust.
- Validation Notes: Service flow confirmed direct return of repository response with no authorization gate. Observed behavior aligned with cross-account readability.
- Recommended Remediation: Add owner/role authorization checks for each billing object and apply deny-by-default behavior.

## F-03

- Title: Admin user listing endpoint lacks function-level authorization
- Severity: Critical
- CWE: CWE-285 (Improper Authorization)
- OWASP API Security Top 10 2023 Mapping: API5:2023 Broken Function Level Authorization
- Affected Component: `AdminController`, `/admin/users`
- Description: The admin endpoint returns user inventory data without authenticating the caller as an admin.
- Technical Root Cause: Missing role check in controller/service path and no framework-level guard (for example, method or route authorization).
- Security Impact: Non-admin or anonymous callers can access privileged operational data.
- Business Impact: Exposure of account inventory and roles lowers attacker effort for privilege targeting and social engineering.
- Validation Notes: Controller review confirmed endpoint response does not depend on token validity or role membership.
- Recommended Remediation: Enforce authenticated admin-only access at framework layer (`@PreAuthorize` or equivalent) and retain service-layer defense in depth.

## F-04

- Title: SQL queries built with untrusted concatenated input
- Severity: Critical
- CWE: CWE-89 (SQL Injection)
- OWASP API Security Top 10 2023 Mapping: API8:2023 Security Misconfiguration
- Affected Component: `UserRepository`, `CustomerRepository`, `BillingRepository`
- Description: SQL statements are constructed by concatenating request-derived values directly into query strings.
- Technical Root Cause: Use of dynamic string construction instead of parameter binding/prepared statements.
- Security Impact: Crafted input can alter query semantics and bypass intended data boundaries.
- Business Impact: Possible unauthorized data access and integrity impact in core customer and auth-related workflows.
- Validation Notes: Repository methods include direct SQL concatenation in lookup paths (`username`, `id`, `accountId`) without parameterization.
- Recommended Remediation: Replace concatenated SQL with parameterized queries (`?`) and strict input type validation before query execution.

## F-05

- Title: Ticket workflow stores and returns untrusted text without safe handling
- Severity: High
- CWE: CWE-79 (Improper Neutralization of Input During Web Page Generation)
- OWASP API Security Top 10 2023 Mapping: API8:2023 Security Misconfiguration
- Affected Component: `TicketService`, `TicketController`, `/tickets`
- Description: Ticket subject and message are stored and returned as raw user input, creating stored/reflected XSS risk in consuming clients.
- Technical Root Cause: No input constraints and no context-aware encoding strategy for downstream rendering.
- Security Impact: If client applications render ticket content in HTML contexts, untrusted script-capable content may execute.
- Business Impact: Potential account/session abuse in support tooling and reputational impact from client-side compromise.
- Validation Notes: Code path confirms direct persistence and retrieval of raw `subject` and `message` fields without sanitization or encoding safeguards.
- Recommended Remediation: Add server-side validation constraints and enforce output encoding/sanitization where data is rendered.

## F-06

- Title: Sensitive secrets are hardcoded in source-controlled configuration
- Severity: High
- CWE: CWE-798 (Use of Hard-coded Credentials)
- OWASP API Security Top 10 2023 Mapping: API8:2023 Security Misconfiguration
- Affected Component: `src/main/resources/application.yml`
- Description: Database password, JWT secret, and external API key are hardcoded in plain text.
- Technical Root Cause: Secret management is implemented as static config values committed to the repository.
- Security Impact: Any repository or artifact access can expose runtime secrets and support token forgery or unauthorized system access.
- Business Impact: Increases probability of credential leakage incidents and emergency rotation work.
- Validation Notes: Configuration review identified plaintext secrets in tracked source files.
- Recommended Remediation: Move secrets to environment-backed secret stores, remove committed values, and rotate exposed credentials.

## F-07

- Title: File upload lacks server-side validation and safe storage controls
- Severity: High
- CWE: CWE-434 (Unrestricted Upload of File with Dangerous Type)
- OWASP API Security Top 10 2023 Mapping: API8:2023 Security Misconfiguration
- Affected Component: `FileStorageService`, `FileUploadController`, `/files/upload`
- Description: Upload handling accepts untrusted content without enforceable type/size constraints and uses user-supplied filenames in storage paths.
- Technical Root Cause: Missing allowlist policy, missing size checks, and direct trust of `MultipartFile` metadata.
- Security Impact: Increased risk of unsafe file hosting, path-related issues, and abuse of storage workflow.
- Business Impact: Operational and incident-response burden if untrusted content enters production systems.
- Validation Notes: Service logic copies uploaded input directly to disk using original filename and no validation control points.
- Recommended Remediation: Enforce extension and MIME allowlists, file size caps, randomized server-generated names, and isolated storage location.

## F-08

- Title: JWT validation does not verify signature integrity or token expiry
- Severity: Critical
- CWE: CWE-347 (Improper Verification of Cryptographic Signature)
- OWASP API Security Top 10 2023 Mapping: API2:2023 Broken Authentication
- Affected Component: `JwtUtil`, identity extraction flow in protected endpoints
- Description: Token handling decodes Base64 payload and trusts claims without cryptographic verification or strict expiry enforcement.
- Technical Root Cause: Custom token parser treats token as trusted serialized data and falls back to anonymous context on parse failure.
- Security Impact: Attackers can alter claims (such as role or user ID) and bypass intended authentication assumptions.
- Business Impact: Core identity and authorization controls become unreliable across the API.
- Validation Notes: Security utility confirms no signature verification step and no enforced rejection path for structurally tampered claim content.
- Recommended Remediation: Use a mature JWT library with required signature verification, strict algorithm policy, and mandatory claim checks (`exp`, `iss`, `aud` as applicable).

## F-09

- Title: Missing input validation baseline across authentication and ticket flows
- Severity: Medium
- CWE: CWE-20 (Improper Input Validation)
- OWASP API Security Top 10 2023 Mapping: API8:2023 Security Misconfiguration
- Affected Component: `AuthController/AuthService`, `TicketController/TicketService`, DTO classes
- Description: Request fields are accepted without consistent server-side validation for format, length, and allowed content.
- Technical Root Cause: DTO-level validation annotations and centralized validation error handling are absent.
- Security Impact: Weakens resilience against malformed input and increases reliability of injection-adjacent abuse attempts.
- Business Impact: Higher defect rate, unstable API behavior under malformed traffic, and increased support overhead.
- Validation Notes: Login and ticket DTOs do not enforce baseline constraints and service layer accepts raw values directly.
- Recommended Remediation: Introduce bean validation (`@Valid`, `@NotBlank`, `@Size`, `@Pattern`) and a standardized request-validation strategy at API boundaries.
