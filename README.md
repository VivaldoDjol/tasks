# TaskFlow – Task Management System (Java/Spring Boot + React)

A full-stack task management application built with Java 17, Spring Boot 3, React, and PostgreSQL.
🚀 Features

    ✅ Task and Task List Management – Create, read, update, and delete tasks organized in lists
    ✅ Progress Tracking – Visual progress indicators for task completion
    ✅ Priority & Status Management – Set task priorities (HIGH/MEDIUM/LOW) and track status (OPEN/CLOSED)
    ✅ PostgreSQL persistence – Uses a production-grade relational database for all task data
    ✅ Full Test Coverage – Unit + Integration tests for backend services
    ✅ Docker-First – One-command setup with PostgreSQL
    ✅ Comprehensive API Documentation – Interactive Swagger UI with detailed examples
    ✅ Production-ready Observability – Health checks, metrics, and logging
    ✅ Modern React Frontend – Built with TypeScript, Vite, and NextUI components

🛠️ Tech Stack
Layer 	Technology
Language 	Java 17, TypeScript
Backend Framework 	Spring Boot 3.3.5
Frontend Framework 	React 18, Vite
Web 	Spring Web, React Router
Data 	Spring Data JPA, PostgreSQL
Testing 	JUnit 5, Mockito, AssertJ
DevOps 	Docker, Docker Compose
Documentation 	SpringDoc OpenAPI (Swagger)
Observability 	Spring Boot Actuator
Build 	Maven, npm
UI Components 	NextUI, Tailwind CSS
📚 API Documentation

Interactive API documentation available at:

    API Documentation: http://localhost:8080/swagger-ui.html
    API Docs JSON: http://localhost:8080/v3/api-docs

Endpoints include:

    POST /api/task-lists - Create a new task list
    GET /api/task-lists - Retrieve all task lists
    GET /api/task-lists/{id} - Retrieve a specific task list
    PUT /api/task-lists/{id} - Update a task list
    DELETE /api/task-lists/{id} - Delete a task list
    POST /api/task-lists/{listId}/tasks - Create a new task
    GET /api/task-lists/{listId}/tasks - Retrieve all tasks in a list
    GET /api/task-lists/{listId}/tasks/{id} - Retrieve a specific task
    PUT /api/task-lists/{listId}/tasks/{id} - Update a task
    DELETE /api/task-lists/{listId}/tasks/{id} - Delete a task
    Actuator endpoints for health and metrics

▶️ Run Locally

Start dependencies (PostgreSQL):

docker-compose up -d

Build and run the application:

# Run backend
cd backend
./mvnw spring-boot:run

# In another terminal, run frontend
cd frontend
npm install
npm run dev

Or with Docker:

docker-compose up -d --build

    Access the application:

    Frontend: http://localhost:5173
    API: http://localhost:8080
    API Documentation: http://localhost:8080/swagger-ui/index.html
    API Docs JSON: http://localhost:8080/v3/api-docs
    Health Check: http://localhost:8080/actuator/health

📁 Project Structure

backend/
├── src/main/java/com/gozzerks/tasks/
│   ├── controllers/       # REST API endpoints
│   ├── domain/
│   │   ├── dto/          # Data Transfer Objects
│   │   └── entities/     # JPA Entities
│   ├── mappers/          # Entity ↔ DTO conversion
│   ├── repositories/     # Database access interfaces
│   ├── services/         # Business logic
│   └── TasksApplication.java  # Main application class
└── pom.xml               # Maven configuration

frontend/
├── src/
│   ├── components/       # React components
│   ├── domain/           # Domain models
│   ├── services/         # API service calls
│   ├── AppProvider.tsx   # Global state management
│   ├── App.tsx           # Main application component
│   └── main.tsx          # Application entry point
├── index.html            # HTML entry point
└── package.json          # npm configuration

🧪 Testing

Run backend unit and integration tests:

cd backend
./mvnw test

Run backend integration tests with Testcontainers (requires Docker):

cd backend
./mvnw verify