# Ticketing Inventory Manager

A Java-based, microservices-oriented ticketing platform designed to manage events, venues, ticket inventory, bookings, and orders.

The project demonstrates backend engineering practices using Spring Boot, Spring Cloud Gateway, PostgreSQL, Apache Kafka, Docker, and Keycloak.

## Architecture

The application is organized into independent services, each responsible for a specific business capability.

### Microservices

| Service | Responsibility |
|---|---|
| **API Gateway** | Central entry point for client requests, routing, and JWT authentication. |
| **Inventory Service** | Manages events, venues, and ticket inventory. |
| **Booking Service** | Handles ticket booking operations. |
| **Order Service** | Handles order processing and booking-related events. |

### Architecture overview
## Technologies

- **Language:** Java
- **Framework:** Spring Boot
- **API Gateway:** Spring Cloud Gateway
- **Security:** Keycloak, OAuth 2.0, JWT
- **Database:** PostgreSQL
- **Messaging:** Apache Kafka
- **Database migrations:** Flyway
- **API documentation:** Springdoc OpenAPI / Swagger UI
- **Containerization:** Docker and Docker Compose
- **Build tool:** Maven

## Project Structure

```text
ticketing-inventory-manager/
│
├── Inventoryservice/
│   ├── src/
│   ├── docker/
│   ├── docker-compose.yaml
│   └── pom.xml
│
├── bookingservice/
│   ├── src/
│   └── pom.xml
│
├── orderservice/
│   ├── src/
│   └── pom.xml
│
├── gateway/
│   ├── src/
│   └── pom.xml
│
├── database/
│   └── sample.sql
│
└── README.md
```

## Prerequisites

Before running the application, ensure you have installed:

- Java 25
- Docker Desktop
- Docker Compose
- Git

Check your Java installation:

```bash
java -version
```

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/iamnana-tyc/ticketing-inventory-manager.git

cd ticketing-inventory-manager
```

### 2. Configure environment variables

The Inventory Service infrastructure uses environment variables for database and Keycloak credentials.

Create a local `.env` file inside the `Inventoryservice/` directory.

Configure the following variables:

```env
POSTGRES_USER=your_database_username
POSTGRES_PASSWORD=your_database_password

KEYCLOAK_DB_PASSWORD=your_keycloak_database_password
KEYCLOAK_ADMIN=your_keycloak_admin_username
KEYCLOAK_ADMIN_PASSWORD=your_keycloak_admin_password
```

Use your own credentials. Do not commit `.env` files or real credentials to GitHub.

### 3. Start the infrastructure

From the project root, run:

```bash
docker compose -f Inventoryservice/docker-compose.yaml up -d
```

This starts the infrastructure services configured in Docker Compose, including PostgreSQL, Kafka, Zookeeper, Kafka UI, Schema Registry, and Keycloak.

Check running containers:

```bash
docker compose -f Inventoryservice/docker-compose.yaml ps
```

### 4. Run the microservices

Run each Spring Boot application using its Maven wrapper.

For example:

```bash
cd Inventoryservice
./mvnw spring-boot:run
```

Run the other services from their respective directories:

```bash
cd bookingservice
./mvnw spring-boot:run
```

```bash
cd orderservice
./mvnw spring-boot:run
```

```bash
cd gateway
./mvnw spring-boot:run
```

Ensure each service's required environment variables, database configuration, and dependent infrastructure are available before starting it.

## API Documentation

The project provides centralized API documentation through the API Gateway using Springdoc OpenAPI and Swagger UI.

Once the API Gateway and the relevant microservices are running, access Swagger UI at:

http://localhost:8091/swagger-ui/index.html

From the Swagger UI definition dropdown, you can select either:

Booking Service – Explore and test booking endpoints.

Inventory Service – Explore and test inventory endpoints.

Both services' API documentation is accessible through a single Swagger UI interface, eliminating the need to access each service's documentation separately.

Authentication

The API Gateway uses OAuth 2.0 Resource Server functionality with JWT authentication.

For protected endpoints, obtain a valid access token from Keycloak and use the Authorize button in Swagger UI to provide the token.

## Database

PostgreSQL is used for persistent data storage.

Flyway manages database schema migrations in the Inventory Service.

Sample database data is provided in the `database/` directory.

## Security

Keycloak is used as the identity and access management solution.

The API Gateway uses OAuth 2.0 Resource Server functionality to validate JWT access tokens and protect secured API routes.

## Messaging

Apache Kafka provides asynchronous communication between services.

This supports event-driven processing and helps decouple booking-related operations from order processing.

## Development

The project uses Maven for dependency management and builds.


## Author

**Nana Owusu Appiah**

GitHub: [iamnana-tyc](https://github.com/iamnana-tyc)
