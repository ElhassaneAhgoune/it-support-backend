# User Authentication Service using JWT

## Project Overview
This project is a **user authentication service** built with **Spring Boot**, **PostgreSQL**, and **JWT** (JSON Web Tokens) for secure access to protected resources. 

The service includes user registration, login, and email verification using OTP (One-Time Password).

The project follows best practices in terms of security, testing, and maintainability, utilizing tools like **Flyway** for database migrations, **MailHog** for email testing, and **Redis** for OTP management.


## Key Features
1. **User Registration**: Register users with a username, email, and password.
2. **User Login**: Authenticate users and generate JWT tokens for secure access.
3. **JWT Authentication**: Protect endpoints by requiring valid JWT tokens.
4. **Email Verification**: Verify user emails using an OTP sent to the registered email address.
5. **Resend Verification Email**: Allows users to request a new email verification link if needed.

## Tech Stack
- **Java 21**
- **Spring Boot 3.3.4 (JPA, Security, Web, Validation, OAuth2 Resource Server, Docker Compose)**
- **JWT (JSON Web Tokens) for Authentication**
- **PostgreSQL** for persistent storage
- **Flyway** for database migrations
- **Redis** for OTP storage
- **MailHog** for email testing
- **Docker Compose** for containerized services
- **Testcontainers** for integration testing with PostgreSQL and Redis
- **Lombok** for reducing boilerplate code

### Prerequisites
- **Java 21** or higher.
- **Docker**
- **Maven**
- **OpenSSL**

### Security Considerations
For demonstration purposes, RSA key pairs are included in the project. **In production environments, always store keys and secrets securely** using tools like **Spring Vault**, **AWS Secrets Manager**, or **HashiCorp Vault**.

### Generating an RSA Key Pair with OpenSSL
To generate a new RSA key pair, use the following commands. Ensure that the keys are stored securely.

Change directory to where the keys should be stored:
```bash
cd src/main/resources/jwt
```

Generate private key:
```bash 
openssl genpkey -algorithm RSA -out app.key -outform PEM
```

Generate public key:
```bash
openssl rsa -pubout -in app.key -out app.pub
```

## Project Structure
```plaintext
src/
 ├── main/
 │   ├── java/
 │   │   └── com/tc/userauth           # Contains application source code
 │   └── resources/
 │       ├── application.yaml          # Main application configuration
 │       ├── db/                       # Flyway migration scripts
 │       └── jwt/                      # RSA keys (app.key and app.pub)
 └── test/
     └── java/                          
         └── com/tc/userauth           # Unit and integration test cases
```

## How to Run the Application

### Database Setup

The database will be created and configured automatically and during application start-up using spring-docker-compose dependency.

### Steps to Run

1. **Clone the Repository**
    ```bash
    git clone git@github.com:dfjmax/user-authentication-service-jwt.git
    cd user-authentication-service-jwt
    ```
2. **Run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Test the APIs
1. Register User
    ```bash
    curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
    }'
    ```
2. Login User
    ```bash
    curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
    "username": "testuser",
    "password": "password123"
    }'
    ```
3. User Profile (Protected)
    ```bash
    curl -X GET http://localhost:8080/api/user/me \
    -H "Authorization: Bearer your.jwt.token.here"
    ```
4. Resend Verification Email
   ```bash
    curl -X POST http://localhost:8080/api/auth/email/resend-verification \
    -d "email=test@example.com"
   ```
5. Resend Verification Email
   ```bash
    curl -X GET "http://localhost:8080/api/auth/email/verify?uid=encryptedUserId&t=otpToken"
   ```

### Email Testing

To view the verification emails sent by the application, visit the MailHog web interface at http://localhost:8025.
