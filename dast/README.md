# DAST Notes

## Goal
Validate runtime security behavior of telecom API endpoints in a local, controlled environment.

## Recommended Tools
- OWASP ZAP (baseline scan + authenticated exploration)
- Postman or curl for deterministic endpoint checks

## Focus Areas
- Authorization bypass on `/admin/users`
- Object-level access control on `/customers/{id}` and `/billing/{accountId}`
- Input handling on `/login` and `/tickets`
- Upload controls on `/files/upload`
- Token validation behavior across protected routes

## Safe Testing Approach
- Use lab-only sample users and sample data.
- Avoid disruptive fuzzing volume.
- Record only defensive observations and remediation actions.

## Expected Deliverables
- Route-level security behavior matrix
- Risk-ranked findings with evidence
- Retest notes after remediation

