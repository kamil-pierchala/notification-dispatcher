# Notification Dispatcher Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%2F4.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Asynchronous, resilient notification dispatching microservice built with **Spring Boot** and **Java 21**. The service decouples notification submission from actual transmission using a dedicated thread pool, implements the **Strategy pattern** for channel extensibility (Email, Discord Webhook), and guarantees delivery resilience with **Spring Retry** and **Exponential Backoff**.

---

## Key Features

- **Asynchronous Processing (`@Async`):** Client requests are acknowledged immediately with HTTP `202 Accepted` and a tracking ID, offloading network I/O to a custom `ThreadPoolTaskExecutor`.
- **Strategy Pattern:** Decoupled notification handlers (`NotificationSender`) allowing effortless addition of new communication channels (e.g., Slack, SMS, Push).
- **Fault Tolerance & Resilience (`Spring Retry`):** Transient network and HTTP delivery failures trigger exponential backoff retries (up to 3 attempts). Terminal failures are captured via `@Recover` and logged with status `FAILED`.
- **Audit & Delivery Tracking:** Full lifecycle tracking (`PENDING` -> `SENT` / `FAILED`) stored in a relational database with detailed error logs and timestamps.
- **RESTful API & OpenAPI Documentation:** Documented via Swagger UI (`/swagger-ui/index.html`) with schema validation.
- **Containerized Environment:** Production-ready multi-stage `Dockerfile` and `docker-compose.yml` integrating PostgreSQL 16.
- **Automated Testing:** Unit test suite using **JUnit 5** and **Mockito**, alongside integration tests verifying HTTP contracts and validation constraints.

---

## Architecture & Asynchronous Flow

```text
 Client (HTTP POST)
         │
         ▼
┌─────────────────────────┐
│ NotificationController  │ ──► Saves initial status [PENDING] into DB
└─────────────────────────┘
         │
         ├──────────────────────────────────────────┐
         │ (Returns 202 Accepted with trackingId)   │ (Dispatches to Thread Pool)
         ▼                                          ▼
   Client Polling                        ┌───────────────────────────────┐
   (GET /api/v1/notifications/{id})       │   NotificationAsyncProcessor  │
                                         └───────────────────────────────┘
                                                        │
                                                        ▼
                                         ┌───────────────────────────────┐
                                         │  Strategy: NotificationSender │
                                         │   (Email / Discord Webhook)   │
                                         └───────────────────────────────┘
                                                        │
                                     ┌──────────────────┴──────────────────┐
                                     │ (Success)                           │ (Transient Failure)
                                     ▼                                     ▼
                        Update status to [SENT]               Spring Retry (Backoff 1s, 2s)
                                                                           │
                                                                           ▼ (All Retries Exhausted)
                                                              Update status to [FAILED] + error log
```

---

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot
- **Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** PostgreSQL 16 (Production/Docker), H2 (Local In-Memory)
- **HTTP Client:** Spring RestClient
- **Resilience:** Spring Retry, Spring AOP
- **Documentation:** Springdoc OpenAPI (Swagger UI)
- **Containerization:** Docker, Docker Compose
- **Testing:** JUnit 5, Mockito, AssertJ, Spring MockMvc

---

## Getting Started
### Prerequisites
- Docker & Docker Compose
- JDK 21+ and Maven 3.9+ (if running locally without Docker)

### Running with Docker Compose (Recommended)
To spin up both the PostgreSQL database and the notification dispatcher service:

```
docker compose up --build -d
```

Once started:
- API Base URL: http://localhost:8080/api/v1/notifications
- Interactive Swagger UI: http://localhost:8080/swagger-ui/index.html

To stop the containers:
```
docker compose down
```

## API Endpoints
### Sample Request Payload (POST /api/v1/notifications)

```
{
  "recipient": "[https://discord.com/api/webhooks/your-webhook-id/token](https://discord.com/api/webhooks/your-webhook-id/token)",
  "channel": "DISCORD",
  "title": "System Alert",
  "content": "Disk usage exceeded 90% threshold."
}
```

---

## Running Automated Tests
Execute the full suite of unit and integration tests:

```
./mvnw clean test
```