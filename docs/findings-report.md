# Security Findings Report

## Assessment Summary
- Project: `application-security-review-lab`
- Target: `app/vulnerable-telecom-api`
- Assessment Type: Manual secure code review with SAST/DAST mindset
- Date: 2026-04-02
- Scope: Authentication, customer data, billing data, support tickets, admin functions, file upload workflow

The application is intentionally vulnerable for training. Findings below are written in production-grade review style.

---

## F-01: Insecure Direct Object Reference (IDOR) in Customer Data Endpoint
- Title: Unauthorized access to customer records by direct identifier manipulation
- Severity: High
- Description: `GET /customers/{id}` accepts arbitrary `id` and returns data without confirming resource ownership.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/CustomerService.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/controller/CustomerController.java`
- Exploit scenario: A low-privilege user requests another customer's ID and receives private profile and account metadata.
- Risk explanation: Exposure of PII and account-linked information can enable privacy breaches and downstream fraud.
- Remediation recommendation: Enforce object-level authorization by checking `customer.ownerUserId == authenticatedUserId` before returning data.

## F-02: Insecure Direct Object Reference (IDOR) in Billing Endpoint
- Title: Unauthorized access to billing account information
- Severity: High
- Description: `GET /billing/{accountId}` returns billing records without validating ownership or access policy.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/BillingService.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/controller/BillingController.java`
- Exploit scenario: An authenticated or anonymous caller iterates account IDs and retrieves billing plans and balances.
- Risk explanation: Financial data leakage creates regulatory and reputational impact.
- Remediation recommendation: Add per-record authorization checks and deny access unless the caller owns the billing account or has an approved privileged role.

## F-03: Broken Authentication/Authorization on Admin Endpoint
- Title: Admin user list exposed without role enforcement
- Severity: Critical
- Description: `GET /admin/users` exposes user data and does not require an authenticated admin role.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/controller/AdminController.java`
- Exploit scenario: Any caller can access privileged user inventory, including usernames and roles.
- Risk explanation: Administrative data disclosure increases attack surface and can support privilege escalation attempts.
- Remediation recommendation: Require valid authentication plus explicit role-based authorization (`ADMIN`) at controller and service layer.

## F-04: SQL Injection via Unsafe Query Construction
- Title: SQL query built with untrusted input via string concatenation
- Severity: Critical
- Description: Repository methods build SQL statements by concatenating external input into query strings.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/repository/UserRepository.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/repository/CustomerRepository.java`
- Exploit scenario: Crafted input can alter SQL semantics and bypass intended query constraints.
- Risk explanation: SQL injection can lead to unauthorized data access and data integrity compromise.
- Remediation recommendation: Use parameterized queries (`?` placeholders) or ORM parameter binding; never concatenate user input into SQL.

## F-05: Stored/Reflected XSS in Ticket Workflow
- Title: Unsanitized ticket fields persist and are returned unencoded
- Severity: High
- Description: `POST /tickets` stores subject/message directly and `GET /tickets` returns raw values.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/TicketService.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/controller/TicketController.java`
- Exploit scenario: User submits script-like markup into ticket content; downstream UI renders unsafe output.
- Risk explanation: Can lead to session theft, unauthorized actions, and defacement in consuming frontends.
- Remediation recommendation: Apply strict input validation and context-aware output encoding before rendering in any UI context.

## F-06: Hardcoded Secrets in Source-Controlled Configuration
- Title: Static credentials and API keys embedded in `application.yml`
- Severity: High
- Description: Database password, JWT secret, and API key are hardcoded in config file.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/resources/application.yml`
- Exploit scenario: Anyone with repository or artifact access can extract live secrets.
- Risk explanation: Secret exposure can enable unauthorized environment access and token forgery.
- Remediation recommendation: Move secrets to environment variables or managed secret stores; rotate compromised credentials.

## F-07: Insecure File Upload Handling
- Title: File upload accepts untrusted content without validation
- Severity: High
- Description: Upload endpoint does not validate MIME type, extension, size, filename safety, or storage policy.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/FileStorageService.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/controller/FileUploadController.java`
- Exploit scenario: Unsupported or dangerous file content is uploaded and stored in server-accessible path.
- Risk explanation: May enable malware hosting, service abuse, and storage-based attacks.
- Remediation recommendation: Enforce allowlist validation, randomize file names, cap file size, and store outside web root.

## F-08: Weak JWT Validation
- Title: Token parsing without cryptographic signature verification
- Severity: Critical
- Description: Token handler decodes Base64 payload and trusts claims without verifying integrity or expiry.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/security/JwtUtil.java`
- Exploit scenario: Token claims can be modified and accepted, enabling role/user identity manipulation.
- Risk explanation: Authentication and authorization guarantees are effectively broken.
- Remediation recommendation: Use signed JWT library validation, enforce algorithm policy, validate signature and expiration, and reject invalid tokens.

## F-09: Missing Input Sanitization and Validation
- Title: Multiple request fields accepted without server-side validation
- Severity: Medium
- Description: Login and ticket inputs are consumed without sanitization, format checks, or boundary constraints.
- Affected code:
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/AuthService.java`
  - `app/vulnerable-telecom-api/src/main/java/com/telecom/vulnerableapi/service/TicketService.java`
- Exploit scenario: Oversized or malformed input degrades reliability and expands injection attack surface.
- Risk explanation: Input control gaps magnify impact of other vulnerabilities.
- Remediation recommendation: Add bean validation (`@Valid`, `@NotBlank`, size limits), canonicalization, and strict business validation rules.

---

## Conclusion
The codebase demonstrates realistic AppSec review targets and a high concentration of high-risk vulnerabilities common in telecom-style backend systems. Priority remediation should focus on:
1. Authentication and authorization integrity
2. SQL injection elimination
3. Secret management and secure upload controls

