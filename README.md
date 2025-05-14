# Getir Bootcamp Project

## Project Overview

This project was created by me as part of the Getir Bootcamp.

The Getir Bootcamp Project is a backend system for managing library-like operations, including book circulation and user
tracking. It emphasizes clean architecture and secure REST APIs.

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 21 and Maven if running without Docker

### Running the App

Run the application locally using Docker Compose:

```bash
docker-compose up --build
```

After successful startup, the application will be accessible at: `http://localhost:8080`

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

You can explore all API endpoints, view schemas, and test requests through this interactive interface.

### Postman Collection

You can access postman collection `GetirBootcamp.postman_collection.json` file under `/assets` folder .

## Technology Stack

This project utilizes a robust and modular backend technology stack centered around Spring Boot and Dockerized
deployment, ensuring scalability, maintainability, and developer productivity.

### Programming Language

- **Java 21** – Modern language features and long-term support

### Backend Framework

- **Spring Boot 3.4.5** – Rapid application development with production-ready defaults

### Web & Reactive

- `spring-boot-starter-web` – For RESTful API development
- `spring-boot-starter-webflux` – Enables reactive endpoints using Reactor

### Persistence

- `spring-boot-starter-data-jpa` – JPA and Hibernate ORM
- **PostgreSQL** – Primary production database
- **H2** – Lightweight in-memory DB for testing and development

### Security

- `spring-boot-starter-security` – Core security layer
- **JWT-based Authentication** with `jjwt` libraries
- Role-based access control for protected resources

### API Documentation

- `springdoc-openapi-starter-webmvc-ui` – Swagger/OpenAPI documentation with interactive UI

### DTO & Mapping

- **MapStruct** – Clean mapping between DTOs and Entities
- **Lombok** – Reduces boilerplate code

### Build & Configuration

- **Maven** – Dependency and build management
- `spring-boot-maven-plugin` – Spring Boot executable JAR packaging
- `maven-compiler-plugin` – Compiler tuning and annotation processing

### Development Tools

- `spring-boot-devtools` – Live reload for local development
- **Docker**, **Docker Compose** – Containerization and orchestration

### Testing Frameworks

- `spring-boot-starter-test` – Core testing support
- `spring-security-test` – Security testing utilities
- `reactor-test` – Testing reactive flows

## Features

- **Authentication & Authorization**
    - JWT-secured login with token issuance
    - Role-based access control

- **Book Management**
    - Add, retrieve, and manage book inventory
    - Reactive controller support for non-blocking reads

- **Circulation Workflow**
    - Tracks borrow, return, and due dates
    - Enforces rules for borrow limits and overdue logic

- **User Management**
    - User creation, lookup, and borrowing status tracking

- **DTO-Driven APIs**
    - Uses MapStruct to decouple entity and API models

- **Global Exception Handling**
    - Managed via `GlobalExceptionHandler` using `@ControllerAdvice`
    - Unified response structure for all errors

- **Centralized Logging with AOP**
    - Method-level logging through `LoggingAspect`
    - Captures method execution time and input parameters

- **Startup Bootstrap**
    - Initial data loading via `AddStartupEntities`

- **API Documentation**
    - Swagger UI interface for live testing and endpoint visibility

- **Clean and Modular Architecture**
    - Controllers, Services, Repositories, DTOs, Filters, Mappers, Exception handlers are all logically separated

## Database Schema

The application tracks users, books, and borrow history using the following structure:

### Entity Relationships

- `users`: Stores system users and their metadata
- `book`: Catalog of books and their availability status
- `circulation`: Links users and books for lending history and due dates

### Schema Diagram

![Database Schema](/assets/getirbootcamp-db-schema.png)

## Additional Information

### Package Structure

```
com.getir.bootcamp
├── advice                # Global exception handling
├── aspect                # Logging with AOP
├── config                # Security, OpenAPI, and CORS configuration
├── controller            # API endpoints
├── dto                   # Request/Response data models
├── entity                # JPA entities
├── exception             # Custom business exceptions
├── filter                # JWT request filtering
├── mapper                # DTO ↔ Entity mappers
├── repository            # Spring Data JPA repositories
├── service               # Business logic services
└── startup               # Sample data initialization
```

### Security

- JWT authentication enforced on protected endpoints
- Token must be passed via `Authorization: Bearer <token>`

### Testing

- Unit tests and integration tests with Spring Boot Test, Security, and Reactor support
