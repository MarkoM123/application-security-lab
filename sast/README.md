# SAST Notes

## Why SAST was used in this review

SAST was used to quickly identify repeatable insecure coding patterns across the Java codebase before manual triage.  
For this project, the highest value came from spotting query construction risks, token-handling anti-patterns, and upload validation gaps.

## What SAST is good at here

- Finding SQL string concatenation patterns in repositories
- Highlighting hardcoded secret-like values in config and code
- Catching obvious unsafe token parsing patterns
- Spotting risky file-upload handling patterns

## What still requires manual review

- Authorization intent and business logic correctness
- Whether access controls are actually enforced per object
- Exploitability context and realistic business impact
- Confirming whether findings are true positives in runtime behavior

## Suggested workflow

1. Run Semgrep rules in `sast/semgrep-rules.yml`.
2. Triage results into likely true positives and likely false positives.
3. Manually verify critical paths in controllers, services, and repositories.
4. Record confirmed findings in `docs/findings-report.md` and `evidence/`.

## Example command

```bash
cd app/vulnerable-telecom-api
mvn -q -DskipTests compile
semgrep --config ../../sast/semgrep-rules.yml src/main/java src/main/resources
```
