# DAST Notes

## What DAST can help validate in this app

DAST is useful here for confirming runtime behavior at API boundaries, especially:

- whether access control decisions are enforced consistently
- whether endpoints leak sensitive data under unexpected caller context
- whether input handling and file upload restrictions are actually active
- whether error responses reveal implementation details

## What DAST cannot reliably prove on its own

- Correctness of internal authorization logic in every code path
- Cryptographic strength and claim-validation quality of JWT implementation
- Root cause and secure remediation details in source code
- Business impact without context from architecture and data flows

## Why manual validation is still required

This API has logic-driven issues (IDOR, role checks, weak token trust) that need source and behavior correlation.  
Manual review is required to confirm exploitability, identify root cause, and produce practical remediation guidance.

## Suggested tooling

- OWASP ZAP for exploratory API testing in a local lab
- Postman/curl for deterministic route checks and retest workflows
