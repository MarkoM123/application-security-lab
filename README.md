# application-security-review-lab

A hands-on Application Security portfolio project built around a deliberately vulnerable Java Spring Boot REST API.

The goal of this lab is to show a practical AppSec workflow: reviewing code, identifying insecure patterns, validating findings, and documenting remediation in a way that reflects how security reviews are often performed in real development environments.

This project is intentionally insecure and is meant for local, educational use only.

---

## Why I built this

I wanted a project that demonstrates more than just “finding bugs.”  
The focus here is on the full application security process:

- understanding the application structure
- reviewing backend code for security issues
- validating risky behavior
- documenting findings clearly
- suggesting realistic remediation

Rather than building a large or overly complex app, I kept the application small enough to review properly, but realistic enough to reflect common backend security mistakes.

---

## What this project covers

This lab is designed to demonstrate practical skills in:

- source code review
- secure coding analysis
- basic SAST/DAST workflow
- vulnerability identification and validation
- remediation planning
- AppSec-style reporting

---

## Application overview

The application simulates a small telecom-style backend API with common business functionality such as:

- authentication
- customer record access
- billing lookups
- support ticket submission
- administrative user listing
- file upload handling

The backend is built with **Java + Spring Boot** and uses an **H2 in-memory database** for simplicity.

Some parts of the application intentionally use insecure patterns to create realistic AppSec review scenarios.

---

## Architecture summary

### Stack
- Java 17
- Spring Boot
- Maven
- H2 database

### Main components
- **Controllers** → expose REST API endpoints
- **Services** → contain business logic
- **Repositories** → interact with the database
- **Security utilities** → intentionally weak token handling for review purposes

### Design approach
The project is intentionally structured like a small internal business API so it can be reviewed the same way a real application assessment might begin: by understanding trust boundaries, data access paths, and where authorization and input handling can break down.

---

## Security issues intentionally included

This project contains a small set of common and interview-relevant application security issues:

- **IDOR** in customer data access
- **IDOR** in billing record access
- **Broken authorization** on admin functionality
- **SQL Injection risk** through unsafe query construction
- **Stored / reflected XSS risk** in support ticket fields
- **Hardcoded secrets** in configuration
- **Insecure file upload handling**
- **Weak JWT/token validation**
- **Missing input sanitization**

These issues were chosen because they are realistic, easy to explain in a code review setting, and useful for demonstrating remediation thinking.

---

## API endpoints

The lab exposes the following endpoints:

- `POST /login`
- `GET /customers/{id}`
- `GET /billing/{accountId}`
- `POST /tickets`
- `GET /tickets`
- `GET /admin/users`
- `POST /files/upload`

---

## Running the lab

### Requirements
- Java 17+
- Maven 3.9+

### Start the application
```bash
cd app/vulnerable-telecom-api
mvn spring-boot:run
