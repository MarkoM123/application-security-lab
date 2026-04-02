# SAST Findings Triage

## Scan context

- Ruleset: `sast/semgrep-rules.yml`
- Codebase: `app/vulnerable-telecom-api/src/main/java` and `src/main/resources`
- Purpose: accelerate pattern discovery before manual validation

## Likely true positives

1. `java-unsafe-sql-concatenation`
   - Hits in repository classes where SQL is assembled with `+`.
   - Matches manual review findings for injection risk.

2. `java-weak-token-base64-parse`
   - Hit in `security/JwtUtil.java`.
   - Consistent with weak token validation finding (decode-only claim trust).

3. `java-unsafe-upload-original-filename`
   - Hit in `service/FileStorageService.java`.
   - Confirms direct use of user-supplied filename in storage path.

4. `yaml-hardcoded-secret-value`
   - Hits in `src/main/resources/application.yml`.
   - Confirms plaintext secret exposure in source-controlled config.

## Likely false positives

- Generic secret regex can also flag non-sensitive placeholders in some projects.
- Base64 usage outside auth contexts may be benign; this rule should be triaged with context.
- Upload filename handling may be acceptable in constrained internal tools, but still warrants policy review.

## Manual confirmation notes

- All likely true positives above were manually reviewed and mapped to findings in `docs/findings-report.md`.
- Severity and business impact were assigned only after manual validation.
- Authorization issues (IDOR, admin access) were primarily identified through manual code and behavior review, not SAST alone.
