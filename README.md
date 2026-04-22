# Assignment — Skill Marketplace API

A secure REST API with JWT authentication and role-based access control, built with Spring Boot.

📖 **Live API Docs:** https://assignment-istb.onrender.com/swagger-ui/index.html

> First request may take ~30 seconds to wake up (free tier cold start).

📖 **Live Frontend:** https://assignment-frontend-5st2.onrender.com

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Frontend | Vanilla JS |
| Security | Spring Security + JWT (JJWT) |
| Database | PostgreSQL (Supabase) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| API Docs | Swagger UI |
| Build | Maven |
| Container | Docker (multi-stage build) |
| CI/CD | GitHub Actions + Render |
| Utilities | Lombok |
| Testing | JUnit 5, Mockito |

---

## Core Features

- User registration & login with **BCrypt password hashing** and **JWT authentication**
- **Role-based access control** — `CONSUMER`, `PROVIDER`, `ADMIN`
- **Login as Admin** — Username: admin, Password: admin123
- **User profile CRUD** — get, update, and delete your own account
- **Global error handling** with custom exceptions and field-level validation errors
- **Swagger UI** for interactive API documentation
- **Docker** support with multi-stage build
- **CI/CD** via GitHub Actions deploying to Render

---

## API Reference

All protected endpoints require: `Authorization: Bearer <token>`

---

### Auth — `/auth` *(public)*

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/create` | Register a new user |
| `POST` | `/auth/login` | Login — returns JWT as plain string |

**Register body:**
```json
{
  "username": "john_doe",
  "password": "securePass1",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "CONSUMER"
}
```

**Login body:**
```json
{
  "username": "john_doe",
  "password": "securePass1"
}
```

**Notes:**
- `username` — 3 to 20 characters, required
- `password` — minimum 8 characters, required
- `userType` — `CONSUMER` or `PROVIDER` (cannot self-register as `ADMIN`)

---

### User — `/public/user` *(authenticated)*

| Method | Path | Description |
|---|---|---|
| `GET` | `/public/user/profile` | Get your own profile |
| `PUT` | `/public/user/update` | Update name, password, or user type |
| `DELETE` | `/public/user/delete` | Delete your account |

**Update body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "password": "newPassword1",
  "userType": "PROVIDER"
}
```

**Response shape (all user endpoints):**
```json
{
  "id": 1,
  "username": "john_doe",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "CONSUMER"
}
```

---

### Health — `/`

```json
{
  "status": "UP"
}
```

---

## Roles

| Role | Description |
|---|---|
| `CONSUMER` | Default user — can manage their own profile |
| `PROVIDER` | Same as consumer at this stage |
| `ADMIN` | Auto-created on startup via `DataInitializer` — not self-registerable |

---

## Error Handling

All errors are handled centrally in `GlobalExceptionHandler`:

| Exception | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `BadRequestException` | 400 |
| `ForbiddenException` | 403 |
| `ConflictException` | 409 |
| `UnauthorizedException` | 401 |
| `MethodArgumentNotValidException` | 400 + field error map |
| `Exception` (catch-all) | 500 |

Validation errors return a map of field → message:
```json
{
  "username": "size must be between 3 and 20",
  "password": "Password is mandatory"
}
```

---

## Project Structure

```
src/main/java/com/Skill/Marketplace/SM/
├── Controllers/
│   ├── AuthController        /auth/**      (public)
│   ├── UserController        /public/user/**
│   └── HealthController      /
├── Services/
│   ├── AuthService
│   ├── UserService
│   └── CustomUserDetailService
├── Entities/
│   ├── UserModel
│   └── UserType              CONSUMER, PROVIDER, ADMIN
├── DTO/
│   └── UserDTO/
│       ├── CreateUserDTO
│       ├── LoginDTO
│       ├── UpdateUserDTO
│       └── ResponseToUser
├── Repo/
│   └── UserRepo
├── Exception/
│   ├── GlobalExceptionHandler
│   ├── BadRequestException
│   ├── ConflictException
│   ├── ForbiddenException
│   ├── ResourceNotFoundException
│   └── UnauthorizedException
└── Security/
    ├── SecurityConfig
    ├── JWTUtil
    ├── JWTAuthFilter
    ├── CorsConfig
    ├── DataInitializer
    └── OpenApiConfig
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 14+

### Environment Variables

```bash
export DB_URL=jdbc:postgresql://localhost:5432/your_db
export DB_USERNAME=your_user
export DB_PASSWORD=your_password
export JWT_SECRET=your_64_char_secret
export JWT_EXPIRATION=3600000
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=adminPassword123
```

Generate a strong JWT secret:
```bash
openssl rand -base64 64
```

> ⚠️ Never commit real secrets to version control.

### Run locally

```bash
./mvnw spring-boot:run
```

| URL | Description |
|---|---|
| `http://localhost:8080` | Health check |
| `http://localhost:8080/swagger-ui.html` | Interactive API docs |

### Run with Docker

```bash
docker build -t assignment-api .

docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/your_db \
  -e DB_USERNAME=your_user \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_secret \
  -e ADMIN_USERNAME=admin \
  -e ADMIN_PASSWORD=admin123 \
  assignment-api
```

### Run Tests

```bash
./mvnw test
```

Tests cover:
- `AuthServiceTest` — login flow with mocked JWT and UserDetailsService
- `UserServiceTest` — user CRUD operations

---

## Environment Variables Reference

| Variable | Required | Default | Description |
|---|---|---|---|
| `DB_URL` | ✅ | — | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | ✅ | — | Database username |
| `DB_PASSWORD` | ✅ | — | Database password |
| `JWT_SECRET` | ✅ | — | HMAC secret key (use 64+ chars) |
| `JWT_EXPIRATION` | ❌ | `3600000` | Token TTL in ms (default: 1 hour) |
| `ADMIN_USERNAME` | ✅ | — | Admin username (auto-created on startup) |
| `ADMIN_PASSWORD` | ✅ | — | Admin password |
