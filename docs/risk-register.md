# Risk Register

| Finding | Severity | Likelihood | Impact | Priority | Owner Suggestion | Recommended Timeline |
| --- | --- | --- | --- | --- | --- | --- |
| IDOR in customer endpoint (`/customers/{id}`) | High | High | High | P1 | API Backend Team Lead | 1-2 sprints |
| IDOR in billing endpoint (`/billing/{accountId}`) | High | High | High | P1 | API Backend Team Lead | 1-2 sprints |
| Admin endpoint missing role enforcement (`/admin/users`) | Critical | High | High | P1 | Security Champion + Backend Lead | Immediate + same sprint |
| SQL injection via query concatenation | Critical | Medium-High | High | P1 | Backend Data Access Owner | Immediate + same sprint |
| Weak JWT validation | Critical | High | High | P1 | Auth Platform Owner | Immediate + same sprint |
| Stored/reflected XSS risk in tickets | High | Medium | Medium-High | P2 | Ticketing Feature Owner | 1 sprint |
| Hardcoded secrets in configuration | High | Medium-High | High | P1 | DevOps + Security Engineering | 1 sprint + key rotation window |
| Insecure file upload handling | High | Medium | High | P2 | Platform Services Team | 1-2 sprints |
| Missing input validation baseline | Medium | High | Medium | P3 | API Backend Team | 2 sprints |
