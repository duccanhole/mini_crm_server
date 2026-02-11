# Mini CRM Backend - Enterprise Relationship Management System

## 🚀 Project Overview
This project serves as the core backend engine for a Customer Relationship Management (CRM) platform. It is designed following modern backend practices, with a strong focus on scalability, security, and real-time user engagement.

The system exposes RESTful APIs to manage the lifecycle of Leads and Customers, tracks interactions through Activities, and delivers real-time updates via an asynchronous, event-driven notification system powered by Server-Sent Events (SSE).

The primary goal of this project is to demonstrate practical backend engineering skills, system design thinking, and product-oriented development, rather than to deliver a fully-featured enterprise CRM solution.

## 🛠 Tech Stack
*   **Framework:** Spring Boot 3.4.2
*   **Language:** Java 25 (LTS)
*   **Security:** Spring Security & Stateless JWT
*   **Persistence:** Spring Data JPA (Hibernate)
*   **Database:** PostgreSQL 16
*   **Real-time:** Server-Sent Events (SSE)
*   **Containerization:** Docker & Docker Compose
*   **Build Tool:** Maven

## 🏗 System Architecture
The application follows a **Layered Architecture (N-Tier)** approach to ensure separation of concerns and maintainability:
1.  **Controller Layer:** Handles RESTful endpoints and request validation.
2.  **Service Layer:** Contains business logic and orchestrates data flow.
3.  **Repository Layer:** Manages data persistence using the Data Access Object (DAO) pattern.
4.  **Security Layer:** Implements stateless authentication and authorization using JWT filters.
5.  **Event/Service Layer:** Orchestrates real-time updates via SSE and scheduled background tasks.

## ✨ Main Features
*   **Lead & Customer Management:** Full CRUD operations with status tracking and lifecycle transitions.
*   **Activity Tracking:** Logging of meetings, calls, and follow-ups associated with leads and customers.
*   **Role-Based Access Control (RBAC):** Secure access managed for Admin, Manager, and Sale roles.
*   **Automated Scheduling:** Background engine for recurring maintenance tasks and automated business logic execution.
*   **Real-time Notifications:** Instant UI updates for critical business events.

## 📡 API Overview
*   **Auth API:** User registration, login, and token management.
*   **User API:** Profile management and administrative user control.
*   **Lead API:** Capture and track potential clients.
*   **Customer API:** Manage converted leads and long-term relationships.
*   **Activity API:** Record and retrieve interaction history.
*   **Notification API:** Fetch and manage user-specific alerts.

## 🔔 Real-time Notification (SSE)
The system utilizes **Server-Sent Events (SSE)** to push notifications to the frontend in real-time.
*   **Efficiency:** Unlike WebSockets, SSE is a lightweight, unidirectional protocol that works over standard HTTP.
*   **Mechanism:** Notifications are triggered by real-time user interactions or background system schedules to ensure proactive engagement.
*   **Resilience:** The implementation handles connection drops and ensures that the client remains synchronized with the latest updates without manual polling.

## 🔐 Authentication Flow (JWT)
The project implements a secure, stateless authentication mechanism:
1.  **Login:** User provides credentials via the `/auth/login` endpoint.
2.  **Token Generation:** Upon successful validation, the server generates a signed JWT containing user claims and roles.
3.  **Client Storage:** The client stores this token (usually in local storage/cookies).
4.  **Authorized Requests:** For subsequent API calls, the client includes the JWT in the `Authorization: Bearer <token>` header.
5.  **Validation:** The `JwtAuthenticationFilter` intercepts requests, validates the signature, and populates the Security Context.

## 📂 Project Structure
```text
src/main/java/com/mini_crm/main/
├── config/       # Security configurations & Bean definitions
├── controller/   # REST Controllers (API Endpoints & SSE Emitters)
├── dto/          # Data Transfer Objects for decoupled API contracts
├── model/        # JPA Entities representing the relational schema
├── repository/   # Data Access Layer (Spring Data JPA)
├── service/      # Business Logic & Transaction Management
├── scheduler/    # Automated background maintenance tasks
├── filter/       # JWT-based Authentication Filters
└── exception/    # Global Exception Handling & Error Mapping
```

## 🗄️ Database Schema Overview
*   **Users:** Manages authentication, roles (Admin, Manager, Sale), and profile data.
*   **Leads:** Captures potential client info and status lifecycle.
*   **Customers:** Stores converted lead data for long-term management.
*   **Activities:** Logs every interaction (Calls, Meetings) linked to Leads or Customers.
*   **Notifications:** Stores alerts that are pushed via SSE.

## ⚙️ How to Run Locally

### Prerequisites
*   Docker & Docker Compose (Recommended)
*   JDK 25 and Maven 3.9+ (For local development)

### Quick Start with Docker
1. Clone the repository.
2. Initialize the environment: copy `.env.example` to `.env` and update the values.
3. Start the system:
   ```bash
   docker-compose up --build -d
   ```
4. API is available at: `http://localhost:8080`.

### Standard Run
1. Ensure a PostgreSQL instance is running.
2. Add `application-local.properties` to the root directory based on the `application-local-example.properties` template.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
#### Security Note
- `JWT_SECRET` must be at least 256 bits (32 bytes) as required by RFC 7518 (HMAC-SHA).
- Use a securely generated random value.

## 🚀 Notes / Future Improvements
*   **Integration Testing:** Implement comprehensive TestContainers-based integration tests.
*   **Caching:** Integrate Redis for session and resource caching.
*   **Logging:** Implement ELK stack for centralized logging and monitoring.
*   **API Documentation:** Integration of Swagger/OpenAPI for interactive documentation.
