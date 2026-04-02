# SAST Notes

## Goal
Static analysis for insecure coding patterns in `vulnerable-telecom-api`, with emphasis on injection, broken auth, and unsafe input handling.

## Recommended Tools
- Semgrep
- SpotBugs
- SonarQube (optional for quality/security dashboarding)

## Suggested Run Commands
```bash
cd app/vulnerable-telecom-api
mvn -q -DskipTests compile
semgrep --config ../../sast/semgrep-rules.yml src/main/java
```

## What to Flag
- String-concatenated SQL
- Missing authorization checks on sensitive endpoints
- Hardcoded credentials/secrets
- Direct use of untrusted request input in sensitive sinks

## Output Handling
- Store findings in a report artifact (JSON/SARIF) for CI trend tracking.
- Mark `High` and `Critical` findings as merge blockers.

