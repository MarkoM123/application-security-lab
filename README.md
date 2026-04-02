# application-security-review-lab

## Project Overview
`application-security-review-lab` is a defensive AppSec portfolio project that simulates a vulnerable telecom backend built with Java Spring Boot.

Purpose:
- demonstrate secure code review skills
- practice SAST/DAST mindset
- identify and explain application vulnerabilities
- provide remediation guidance for Java backend issues

This project is intentionally insecure and should only be run in a local lab environment.

## Architecture
- Application type: Spring Boot REST API
- Domain: Telecom backend (authentication, customer data, billing, support tickets, admin users, file uploads)
- Data layer: H2 in-memory database + intentionally unsafe query patterns
- Auth model: intentionally weak token handling for training

Main components:
- `controller`: API endpoints
- `service`: business logic (intentionally insecure in places)
- `repository`: database access with unsafe examples
- `security`: weak JWT parsing utilities

## Project Structure
```text
application-security-review-lab/
  app/
    vulnerable-telecom-api/
  docs/
    findings-report.md
    remediation-guide.md
  code-review/
    java-findings.md
  sast/
    README.md
    semgrep-rules.yml
  dast/
    README.md
    manual-test-checklist.md
```

## Implemented Vulnerabilities
1. IDOR in `GET /customers/{id}`
2. IDOR in `GET /billing/{accountId}`
3. Broken authorization in `GET /admin/users`
4. SQL Injection via unsafe query concatenation
5. Stored/Reflected XSS in `POST /tickets`
6. Hardcoded secrets in configuration
7. Insecure file upload in `POST /files/upload`
8. Weak JWT validation (no signature/expiry enforcement)
9. Missing input sanitization

## API Endpoints
- `POST /login`
- `GET /customers/{id}`
- `GET /billing/{accountId}`
- `POST /tickets`
- `GET /tickets`
- `GET /admin/users`
- `POST /files/upload`

## How To Run
Prerequisites:
- Java 17+
- Maven 3.9+

Run:
```bash
cd app/vulnerable-telecom-api
mvn spring-boot:run
```

App defaults:
- Base URL: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`

## Safe Vulnerability Testing (Educational)
- IDOR: authenticate as one sample user, then request another user's `customer` or `billing` identifier to verify missing ownership checks.
- Broken authorization: call `/admin/users` with no token or non-admin token and observe exposed data.
- SQL Injection risk: provide SQL-tainted input in login/customer identifiers and observe query behavior in this controlled lab.
- XSS risk: submit ticket fields containing HTML-like input and observe unsanitized storage/echo behavior.
- Weak JWT: inspect token format and verify that tampering is not cryptographically detected in the current implementation.
- File upload: upload unsupported file types and observe acceptance due to missing server-side validation.

Do not run these tests against systems you do not own or have permission to assess.

## Tools Used (SAST / DAST)
SAST:
- Semgrep
- SpotBugs
- SonarQube (optional)

DAST:
- OWASP ZAP
- Postman or curl for endpoint behavior validation

See:
- `sast/README.md`
- `dast/README.md`
- `dast/manual-test-checklist.md`

## Documentation
- Security findings report: `docs/findings-report.md`
- Remediation guide: `docs/remediation-guide.md`
- Code review notes and secure rewrites: `code-review/java-findings.md`

