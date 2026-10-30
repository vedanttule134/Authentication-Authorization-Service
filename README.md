# Spring Security Authentication & Authorization API

A Java backend project exploring authentication and access control with Spring Boot, Spring Security, JWT, and MySQL. The application supports user registration, username/password login, JWT-based request authentication, database-backed refresh-token sessions, and role-based access to user and employee APIs.

The repository is intended as a practical learning and portfolio project. It demonstrates the core security flow while documenting the work still required before production use.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-6DB33F?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-7.x-6DB33F?logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/Authentication-JWT-black?logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)

## What the application currently does

- Registers users and hashes passwords with BCrypt.
- Authenticates username/password credentials through Spring Security's `AuthenticationManager`.
- Issues signed JWT access and refresh tokens.
- Reads bearer access tokens in a `OncePerRequestFilter` and establishes the Spring Security context.
- Persists refresh-token sessions in MySQL.
- Limits the number of stored sessions per user (currently two; the oldest session is removed when the limit is reached).
- Provides role-based access to user and employee endpoints.
- Maps roles to application permissions in a central Java mapping.
- Exposes employee create, read, update, partial-update, and delete operations.

## Technology stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Security | Spring Security 7, JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA, Hibernate, MySQL |
| Object mapping | ModelMapper |
| Boilerplate reduction | Lombok |
| Build | Maven / Maven Wrapper |

## Project structure

```text
src/main/java/com/security/SpringSecurityApplication/
├── config/          # SecurityFilterChain and application beans
├── controllers/     # Authentication, user, and employee REST controllers
├── entities/        # User, session, and employee entities
├── enums/           # Role, permission, and department enums
├── filters/         # JWT request authentication filter
├── repositories/    # Spring Data JPA repositories
├── requestDTO/      # Request payloads
├── responseDTO/     # Response payloads
├── serviceImpl/     # User service implementation
├── services/        # Authentication, JWT, session, and employee services
└── utils/            # Role-to-permission mapping
```

## Authorization model

The current role-to-permission mapping is defined in code (`permissionMappings`), rather than managed dynamically in database tables.

| Role | Current employee access | User API access |
|---|---|---|
| `ADMIN` | Read and write | User endpoints |
| `USER` | Read only | Not allowed |
| `CREATOR` | Permission mapping includes view and create; current URL rules still restrict employee writes to ADMIN | Not allowed |

The `Permission` enum and mapping include authorities such as `EMP_VIEW`, `EMP_CREATE`, `EMP_UPDATE`, `EMP_DELETE`, `USER_CREATE`, and `USER_VIEW`. The active `SecurityFilterChain` currently applies URL-level role rules; do not assume every mapped permission is independently enforced by method-level annotations.

## API reference

Base URL: `http://localhost:8080`

| Method | Endpoint | Access configured in `SecurityConfig` | Purpose |
|---|---|---|---|
| `POST` | `/api/user/create` | Public | Register a user |
| `POST` | `/api/auth/login` | Public | Authenticate and issue tokens |
| `POST` | `/api/auth/refresh` | Public; requires refresh-token cookie | Obtain a new access token |
| `POST` | `/api/auth/logout` | Public; requires refresh-token cookie | Delete the matching stored session |
| `GET` | `/api/user` | ADMIN | List users |
| `GET` | `/api/employee` | USER, ADMIN | List employees |
| `GET` | `/api/employee/{id}` | USER, ADMIN | Get an employee |
| `POST` | `/api/employee/add` | ADMIN | Create an employee |
| `PUT` | `/api/employee/update` | ADMIN | Update an employee |
| `PATCH` | `/api/employee/edit/{id}` | ADMIN | Partially update an employee |
| `DELETE` | `/api/employee/delete/{id}` | ADMIN | Delete an employee |

### Example requests

**Register a user**

```http
POST /api/user/create
Content-Type: application/json
```

```json
{
  "username": "demo.user",
  "email": "demo@example.com",
  "password": "ChangeThisPassword123!",
  "roles": ["USER"]
}
```

**Login**

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "demo.user",
  "password": "ChangeThisPassword123!"
}
```

The login response contains the access token and username. The controller also sets a `refreshToken` cookie. Send the cookie when calling `/api/auth/refresh` or `/api/auth/logout`. For protected endpoints, send the access token as:

```http
Authorization: Bearer <access-token>
```

**Create an employee (ADMIN)**

```http
POST /api/employee/add
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "name": "Alex Morgan",
  "age": 30,
  "title": "Software Engineer",
  "dept": "ENGINEERING",
  "salary": 85000.0
}
```

Use a department value defined in the `Department` enum in the source code.

## Run locally

### Prerequisites

- JDK 21
- MySQL 8
- Maven (or use the included Maven Wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/vedanttule134/SpringSecurityApplication.git
cd SpringSecurityApplication
```

If your GitHub repository uses a different name, update the clone URL and directory accordingly.

### 2. Create the database

```sql
CREATE DATABASE spring_security;
```

### 3. Configure database credentials and JWT signing key

The application reads its current configuration from `src/main/resources/application.properties`. Configure the following values for your local environment:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_security
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
jwt.secretKey=${JWT_SECRET_KEY}
```

Set `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET_KEY` in your environment before starting the application. Use a sufficiently long, randomly generated signing key compatible with the HMAC algorithm used by JJWT. Never commit real credentials, signing keys, or production secrets.

> The checked-in application configuration should be reviewed before publishing. Replace any local credentials or signing keys with environment-variable placeholders and rotate any secret that has already been committed or shared.

### 4. Start the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

The application starts on port `8080` by default.

## Authentication flow

```mermaid
sequenceDiagram
    participant Client
    participant Auth as AuthController
    participant Manager as AuthenticationManager
    participant Details as CustomUserDetailsService
    participant DB as MySQL

    Client->>Auth: POST /api/auth/login
    Auth->>Manager: Authenticate username/password
    Manager->>Details: Load user details
    Details->>DB: Find user by username
    DB-->>Details: User record and password hash
    Manager-->>Auth: Authentication result
    Auth->>DB: Persist refresh-token session
    Auth-->>Client: Access token + refresh-token cookie
```

For protected requests, `JwtAuthFilter` reads the bearer token, verifies its signature and expiry through `JwtService`, loads the user, checks that the account is enabled, and places the user's authorities in the security context. The configured URL authorization rules then decide whether the request is allowed.

## Current scope and known limitations

This is an evolving project, not a production-ready identity provider. The following items should be addressed before using it to protect real applications:

- Refresh-token rotation and reuse detection are not implemented; the current refresh flow reuses the submitted refresh token.
- Refresh-token storage currently uses the raw token. Store a one-way hash instead.
- The refresh cookie currently sets `HttpOnly`, but should also be configured with appropriate `Secure`, `SameSite`, `Path`, and expiry attributes. Review CSRF protections when using cookie-based refresh tokens.
- Public registration currently accepts a roles field. Restrict public registration to a default low-privilege role and reserve role assignment for authorized administrators.
- `CustomUserDetailsService` currently assigns the `USER` role rather than loading the user's persisted roles. Align it with the database-backed role and permission model.
- The session validation path should handle unknown, expired, and revoked sessions safely; session expiry and cleanup are not yet implemented.
- Access and refresh tokens currently use the same signing key and do not have an explicit token-purpose claim. Separate token purposes and validate issuer/audience as appropriate.
- Automated authentication/authorization tests, centralized API error responses, schema migrations, API documentation, containerization, and CI checks remain to be added.

## Development roadmap

- [ ] Correctly load persisted roles and authorities during authentication.
- [ ] Prevent privilege escalation through public registration.
- [ ] Implement refresh-token rotation, reuse detection, hashed storage, and expiry.
- [ ] Add session listing, individual-session revocation, and logout from all devices.
- [ ] Add password reset, email verification, and login rate limiting/account lockout.
- [ ] Add method-level permission checks and database-managed roles/permissions.
- [ ] Add validation and consistent `400`, `401`, `403`, and `404` error responses.
- [ ] Add unit and integration tests with JUnit 5, Mockito, MockMvc, and Testcontainers.
- [ ] Add Flyway migrations, OpenAPI documentation, Docker Compose, and GitHub Actions.

## Contributing

Issues and pull requests are welcome. For substantial changes, please open an issue describing the proposed behavior before submitting a pull request.

## License

No license is currently specified. Add a `LICENSE` file before inviting others to reuse or distribute this project.
