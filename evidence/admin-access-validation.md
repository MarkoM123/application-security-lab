# Validation Evidence: Admin Access Control

## Endpoint reviewed

- `GET /admin/users`

## What was tested conceptually

- Whether admin-only functionality requires authenticated admin role.
- Whether route access decision changes for anonymous vs standard user context.

## Observed insecure behavior

- Endpoint returns user listing without enforcing admin role.
- Access is not gated by a robust authentication and authorization check in controller/service path.

## Why the finding was considered valid

- Controller logic directly returns `adminService.getAllUsers()` with no role enforcement.
- Runtime observation confirmed privileged data exposure outside intended administrative boundary.
