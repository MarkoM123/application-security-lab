# Security Review Methodology

## Scope definition

The review focused on the Java API implementation and supporting documentation in this repository, with emphasis on authentication, authorization, data access, input handling, and file upload controls.

Primary scope:

- API controllers, services, repositories, and security utilities
- Runtime behavior that can be validated locally
- Security findings that materially affect confidentiality, integrity, or access control

## Code review approach

- Performed endpoint-to-sink tracing for each exposed route.
- Reviewed authorization decisions at controller and service boundaries.
- Inspected token handling for trust decisions and claim validation.
- Evaluated repository query construction for injection risk.
- Reviewed upload and ticketing paths as untrusted-content flows.

## Static review approach

- Prepared Semgrep starter rules to highlight likely insecure patterns.
- Used static findings as triage input, not as final proof.
- Prioritized likely true positives in security-critical components.

## Manual validation approach

- Confirmed findings using controlled local requests and seeded data.
- Focused validation on access control outcomes and data exposure behavior.
- Captured defensive validation notes in the `evidence/` directory.
- Avoided offensive automation and avoided exploit step-by-step guidance.

## Risk rating approach

Severity reflects qualitative likelihood and impact:

- `Critical`: direct compromise of trust boundaries, admin functions, or auth integrity
- `High`: clear unauthorized data access or serious misconfiguration with broad impact
- `Medium`: meaningful control weakness that amplifies other risks
- `Low`: limited direct impact but still relevant for defense-in-depth

Business context considered:

- exposure of customer or billing records
- impact on operational trust (admin functions)
- potential for unauthorized access persistence

## Documentation process

- Consolidated high-level outcomes in `docs/executive-summary.md`
- Logged detailed findings in `docs/findings-report.md`
- Produced developer-oriented fixes in `docs/remediation-guide.md`
- Captured source-level review notes in `code-review/java-findings.md`
- Added validation notes and secure rewrite examples for interview-ready context

## Assumptions and limitations

- Assessment was performed on a local lab application, not production infrastructure.
- No deep infrastructure penetration testing was in scope.
- Third-party dependencies were not exhaustively audited for CVEs.
- Findings are based on observed code and local behavior as of 2026-04-02.
