# Validation Evidence: File Upload Controls

## Endpoint reviewed

- `POST /files/upload`

## What was tested conceptually

- Whether upload policy enforces file type, extension, and size restrictions.
- Whether user-supplied filenames influence server storage path.
- Whether response leaks internal filesystem details.

## Observed insecure behavior

- Upload accepts files without strict allowlist checks.
- Original filename is used directly for destination path resolution.
- Internal storage path is returned in API response.

## Why the finding was considered valid

- Source code confirms direct trust in multipart metadata with no hardening controls.
- Behavior is sufficient to classify the issue as insecure upload handling and security misconfiguration.
