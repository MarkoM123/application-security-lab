# Validation Evidence: Ticket Content XSS Exposure

## Endpoint reviewed

- `POST /tickets`
- `GET /tickets`

## What was tested conceptually

- Whether ticket fields are constrained/sanitized before storage.
- Whether returned ticket content is encoded for safe rendering contexts.

## Observed insecure behavior

- Ticket `subject` and `message` are persisted as received.
- Ticket content is returned to clients without output safety controls.
- This creates stored/reflected XSS exposure if downstream UI renders fields in HTML context.

## Why the finding was considered valid

- Service logic confirms direct assignment of untrusted fields into persistent objects.
- The API response path preserves those raw values, making downstream rendering risk credible and actionable.
