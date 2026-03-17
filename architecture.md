# Todo Application Architecture

## Overview

The Todo application is a full-stack web application built with a modern architecture consisting of a frontend user interface, a backend REST API, and a relational database. The application allows users to create, view, and manage todo tasks through a simple web interface.

## Architecture Diagram

```
┌─────────────────┐    HTTP     ┌─────────────────┐    JDBC     ┌─────────────────┐
│                 │ ──────────► │                 │ ──────────► │                 │
│   Frontend      │             │   Backend       │             │   Database      │
│   (Nginx)       │ ◄────────── │   (Spring Boot) │ ◄────────── │   (MySQL)       │
│                 │             │                 │             │                 │
└─────────────────┘             └─────────────────┘             └─────────────────┘
```

## Components

### Frontend
- **Technology**: HTML5, CSS3, Vanilla JavaScript
- **Server**: Nginx web server
- **Responsibilities**:
  - User interface for task management
  - Client-side form validation
  - AJAX calls to backend REST API
  - Responsive design for mobile and desktop

### Backend
- **Technology**: Spring Boot 3.x with Java 17
- **Framework**: Spring Web, Spring Data JPA, Spring Validation
- **Architecture**: RESTful API with layered architecture
- **Components**:
  - **Controller Layer**: `TaskController` - REST endpoints
  - **Service Layer**: `TaskService` - Business logic
  - **Repository Layer**: `TaskRepository` - Data access
  - **DTOs**: `TaskDto`, `TaskRequest` - Data transfer objects
  - **Exception Handling**: `ApiExceptionHandler` - Global error handling

### Database
- **Technology**: MySQL 8
- **Schema**:
  - `task` table with columns: id, title, description, created_at, completed
- **ORM**: Hibernate with JPA
- **Configuration**: Automatic schema updates via `hibernate.ddl-auto: update`

## Data Flow

1. **Task Creation**:
   - User submits form in frontend
   - Frontend sends POST request to `/api/tasks`
   - Backend validates input and creates task entity
   - Task is persisted to MySQL database
   - Backend returns created task DTO
   - Frontend updates UI with new task

2. **Task Retrieval**:
   - Frontend loads or refreshes task list
   - Frontend sends GET request to `/api/tasks`
   - Backend queries database for recent incomplete tasks
   - Backend returns list of task DTOs
   - Frontend renders tasks in the UI

3. **Task Completion**:
   - User clicks "Mark Done" on a task
   - Frontend sends POST request to `/api/tasks/{id}/done`
   - Backend updates task status in database
   - Frontend removes completed task from UI

## Technologies Used

### Backend
- **Java**: 17 (Eclipse Temurin)
- **Spring Boot**: 3.x
- **Spring Data JPA**: For data persistence
- **Hibernate**: ORM framework
- **MySQL Connector/J**: Database driver
- **Gradle**: Build tool
- **JUnit 5**: Unit testing
- **Testcontainers**: Integration testing

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Styling with Flexbox/Grid
- **JavaScript (ES6+)**: DOM manipulation and API calls
- **Nginx**: Web server and reverse proxy

### Infrastructure
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **MySQL**: Relational database

## Deployment

The application is containerized using Docker with the following services:

- **db**: MySQL 8 database with persistent volume
- **backend**: Spring Boot application built with Gradle
- **frontend**: Static files served by Nginx

### Docker Configuration
- Multi-stage build for optimized backend image
- Health checks for database service
- Service dependencies managed via `depends_on`
- Port mappings: 80 (frontend), 8080 (backend)

## API Specification

### Endpoints

- `GET /api/tasks` - Retrieve recent incomplete tasks
- `POST /api/tasks` - Create a new task
- `POST /api/tasks/{id}/done` - Mark task as completed

### Request/Response Formats

All endpoints use JSON format with the following DTOs:

```json
// TaskDto
{
  "id": 1,
  "title": "Task Title",
  "description": "Task Description",
  "createdAt": "2024-01-01T10:00:00",
  "completed": false
}

// TaskRequest
{
  "title": "Task Title",
  "description": "Task Description"
}
```

## Security Considerations

- CORS enabled for cross-origin requests
- Input validation using Bean Validation
- SQL injection prevention via JPA/Hibernate
- No authentication/authorization implemented (suitable for demo purposes)

## Development and Testing

- **Build**: Gradle wrapper for consistent builds
- **Testing**: Unit tests with JUnit 5, integration tests with Testcontainers
- **Code Quality**: Exception handling with custom exceptions
- **Configuration**: Environment-based configuration via Spring profiles

## Scalability

- Stateless backend services
- Database connection pooling via HikariCP (Spring Boot default)
- Horizontal scaling possible by running multiple backend instances
- Static frontend can be served via CDN for global distribution