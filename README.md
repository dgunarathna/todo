## Todo App – Spring Boot, MySQL, Vanilla JS

### Overview

This project is a small full-stack todo application with:

- **Backend**: Spring Boot (Gradle), REST API under `/api/tasks`
- **Database**: MySQL (`task` table managed via JPA/Hibernate)
- **Frontend**: Vanilla HTML/CSS/JS SPA served by Nginx
- **Orchestration**: `docker-compose` starting three services: `db`, `backend`, `frontend`

Users can:

- Create tasks with a title and optional description
- See only the **5 most recent incomplete tasks**
- Mark tasks as done; completed tasks disappear from the list

### Architecture

- `db` – MySQL 8 instance
- `backend` – Spring Boot app that exposes REST endpoints and uses Spring Data JPA
- `frontend` – Nginx serving a static SPA, proxying `/api/*` to the backend

Main components:

- Backend code: `src/main/java/com/todo/todo`
- Backend config: `src/main/resources/application.yml`
- Frontend SPA: `frontend/index.html`, `frontend/styles.css`, `frontend/app.js`
- Docker Compose file: `docker-compose.yml`

### Running with Docker

#### Prerequisites

- Docker
- Docker Compose (if not bundled with Docker)

#### Start the stack

From the project root:

```bash
docker compose up --build
```

This will:

- Start **MySQL** on port `3306`
- Build and start the **Spring Boot backend** on port `8080`
- Build and start the **Nginx frontend** on port `80`

Once all containers are healthy, open the app in your browser:

- `http://localhost`

The frontend will communicate with the backend via `/api/tasks` (proxied by Nginx to the backend container).

#### Stopping the stack

```bash
docker compose down
```

To remove volumes (including the MySQL data volume):

```bash
docker compose down -v
```

### Backend – API

Base URL (from frontend or browser): `/api/tasks`

- **GET `/api/tasks`**
  - Returns a JSON array of up to **5 most recent, incomplete** tasks.
  - Example response:
    ```json
    [
      {
        "id": 1,
        "title": "Buy milk",
        "description": "2L whole milk",
        "createdAt": "2026-03-15T12:34:56.789",
        "completed": false
      }
    ]
    ```

- **POST `/api/tasks`**
  - Creates a new task.
  - Request body:
    ```json
    {
      "title": "Task title",
      "description": "Optional description"
    }
    ```
  - Responses:
    - `201 Created` with created task JSON on success
    - `400 Bad Request` if validation fails (e.g., empty title)

- **POST `/api/tasks/{id}/done`**
  - Marks a task as completed.
  - Responses:
    - `204 No Content` on success
    - `404 Not Found` if the task does not exist

### Backend – Local Development

#### Running tests

From the project root:

```bash
./gradlew test
```

This runs:

- **Unit tests** for `TaskService`
- **Integration tests** for `TaskController` using Spring Boot test + MockMvc

#### Running the backend without Docker

You can run the Spring Boot app locally (e.g., pointing at a local MySQL instance):

1. Ensure MySQL is running and a database is available
2. Configure environment variables or override `application.yml` values:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Start the app:
   ```bash
   ./gradlew bootRun
   ```

### Frontend

The frontend is a simple SPA in `frontend/`:

- `index.html` – page skeleton:
  - Header and description
  - Task creation form (title + description)
  - Task list container showing up to 5 pending tasks
- `app.js` – behavior:
  - On load: `GET /api/tasks`, render task cards
  - On form submit: `POST /api/tasks`, then refresh list
  - On “Done” click: `POST /api/tasks/{id}/done`, then remove from list
  - Basic error messages for failed requests
- `styles.css` – styling:
  - Centered layout with card-style UI
  - Responsive design for mobile and desktop

When running via Docker Compose, the frontend is served at `http://localhost`.

### Database

The application uses a single table `task` in MySQL, managed by JPA/Hibernate. In Docker, schema is created automatically (`ddl-auto=update`).

Conceptually, the table:

- `id BIGINT AUTO_INCREMENT PRIMARY KEY`
- `title VARCHAR(255) NOT NULL`
- `description TEXT NULL`
- `created_at DATETIME NOT NULL`
- `completed TINYINT(1) NOT NULL DEFAULT 0`

### Notes & Assumptions

- No authentication/authorization is implemented.
- Only the last 5 **incomplete** tasks are returned from the API and shown in the UI.
- Error handling is minimal but returns appropriate HTTP status codes and simple JSON error messages.

