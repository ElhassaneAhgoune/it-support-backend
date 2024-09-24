# User Authentication Service using JWT

## Project Overview
This project is a **user authentication service** that allows users to register, log in, and obtain JWT tokens for secure access to protected resources.

The service is built with **Spring Boot** and **PostgreSQL**, utilizing **JWT** (JSON Web Tokens) for authentication. 

**Spring Security** is used to secure the APIs, and **Flyway** manages database migrations.

## Key Features
1. **User Registration**: Register users with a username, email, and password.
2. **User Login**: Authenticate users and generate a JWT token.
3. **JWT Authentication**: Protects endpoints by requiring a valid JWT token.

## Tech Stack
- **Java 21**
- **Spring Boot 3.3.4 (JPA, Security, Web, Validation, OAuth Resource Server, Docker Compose)**
- **Maven**
- **PostgreSQL**
- **Lombok**
- **Flyway**
- **Docker**

### Prerequisites
- **Java 21** or higher.
- **Docker**
- **Maven**
- **OpenSSL**

### Generating an RSA Key Pair with OpenSSL
In this project, I have provided a sample RSA public-private key pair for demonstration purposes. 

However, for production use, store them securely outside the project directory to ensure the security of your application.

Follow the steps below to generate a new RSA key pair using OpenSSL.

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
- `src/main/java`: Contains application source code (controllers, services, entities).
- `src/main/resources`: Configuration files and database migration scripts.
- `src/test/java`: Unit and integration test cases.

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
   ```console
   mvn clean install
   mvn spring-boot:run
   ```

### Access the Application
1. Registration API
    ```bash
    curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
    }'
    ```
2. Login API
    ```bash
    curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
    "username": "testuser",
    "password": "password123"
    }'
    ```
3. User Profile API (Protected)
    ```bash
    curl -X GET http://localhost:8080/api/user/me \
    -H "Authorization: Bearer your.jwt.token.here"
    ```