# School API

A REST API to manage **schools** and **students** built with Java, Spring Boot, and PostgreSQL.

**Tech stack:** Java 21 · Spring Boot 3.5 · Gradle · PostgreSQL · Docker

---

## What it does

- Create, update, and delete schools (with unique names and capacity limits 50-2000)
- Manage students within schools (each student belongs to exactly one school)
- Search schools by name (ILIKE matching)
- Search students by name within a school (ILIKE matching)
- View school details with enrolled student count
- View student details
- Paginated results for all list endpoints
- Proper error handling with meaningful HTTP status codes

---

## Quick start

```bash
# Build and run with Docker Compose
docker build -f docker/Dockerfile -t school-api:latest .
docker compose up -d

# Check it's running
curl http://localhost:8080/actuator/health

# Open Swagger UI for API documentation and testing
open http://localhost:8080/swagger-ui.html
```

The `docker-compose.yml` sets up PostgreSQL 15 and the API with proper environment variables.

---

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/schooldb` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile |

---

## API endpoints

### Schools
- `POST /api/schools` - Create a school
- `GET /api/schools` - List schools (with search by name)
- `GET /api/schools/{id}` - Get school details with enrolled count
- `PUT /api/schools/{id}` - Update a school
- `DELETE /api/schools/{id}` - Delete a school

### Students
- `POST /api/schools/{schoolId}/students` - Add student to school
- `GET /api/schools/{schoolId}/students` - List students in school (with search by name)
- `GET /api/schools/{schoolId}/students/{studentId}` - Get student details
- `PUT /api/schools/{schoolId}/students/{studentId}` - Update student
- `DELETE /api/schools/{schoolId}/students/{studentId}` - Remove student

All endpoints support pagination with `page` and `size` parameters.

---

## Error handling

The API returns appropriate HTTP status codes:
- `400 Bad Request` - Validation errors (invalid capacity, blank names)
- `404 Not Found` - School or student not found
- `409 Conflict` - Duplicate school name or school at maximum capacity
- `422 Unprocessable Entity` - Optimistic locking conflicts

---

## Example usage

```bash
# Create a school
curl -X POST http://localhost:8080/api/schools \
  -H 'Content-Type: application/json' \
  -d '{"name":"Hogwarts","capacity":500}'

# Add a student
curl -X POST http://localhost:8080/api/schools/{schoolId}/students \
  -H 'Content-Type: application/json' \
  -d '{"name":"Hermione Granger"}'

# Search schools by name
curl "http://localhost:8080/api/schools?query=hog&page=0&size=10"

# Search students in a school
curl "http://localhost:8080/api/schools/{schoolId}/students?query=herm&page=0&size=10"
```

---

## Architecture notes

This project uses:
- **Hexagonal architecture** with clear separation between domain, application, and adapters
- **Manual DTO mapping** instead of MapStruct for simplicity
- **Liquibase** for database migrations
- **Hybrid approach** balancing DDD purity with performance optimizations

---

## Docker image

The application is delivered as a runnable Docker image:
- Multi-stage build for optimized image size
- Based on Eclipse Temurin JRE 21
- Exposes port 8080
- Includes health check endpoint

---

## Future Work

This project was developed within a **2-day time allocation**. Since [commit fa73308](https://github.com/hectorherranz/school-challange/commit/fa73308aba8e393ac7493e94a87f43ce62b97168), the API is fully functional and usable, but several architectural improvements and optimizations were left for future iterations:

### Pending Tasks

- **Move `@Transactional` from JPA adapters to application handlers** to enforce proper transaction boundaries
- **Decouple application handlers from adapter services**; depend only on `application.port.out` interfaces
- **Implement domain event publishing** (finish `SpringEventPublisher.publish`, call `pullDomainEvents()` in handlers) and relocate `DomainEventPublisher` to `application.port.out` (It's actually out of scope)
- **Stop leaking Spring `Pageable` into application**; use app-specific pagination DTOs and enforce max page size + default sorting
- **Add a case-insensitive unique constraint on `schools.name` and `pg_trgm` GIN indexes for ILIKE searches** to ensure integrity and performance
- **... and other refinements**

These improvements would enhance the architecture's purity, performance, and maintainability while maintaining the current functionality.

---

## License

MIT License - see the `LICENSE` file for details.
