# Microservices Setup and Run Guide

## Prerequisites

Ensure you have installed:
- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git** (optional)

## Step 1: Set Up PostgreSQL Database

### Option A: Local PostgreSQL Installation
```bash
# Create database
createdb -U postgres users

# Verify connection
psql -U postgres -d users
```

### Option B: Docker (Recommended)
```bash
# Run PostgreSQL in Docker
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=siwar00siwar \
  -e POSTGRES_DB=users \
  -p 5432:5432 \
  -d postgres:15
```

## Step 2: Configure Application Properties

Both services are pre-configured with correct properties. Verify:

### Admin Service (`admin-service/src/main/resources/application.properties`)
```properties
spring.application.name=admin-service
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/users
spring.datasource.username=postgres
spring.datasource.password=siwar00siwar
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# User Service URL for REST calls
user.service.url=http://localhost:8081
```

### User Service (`user-service/src/main/resources/application.properties`)
```properties
spring.application.name=user-service
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/users
spring.datasource.username=postgres
spring.datasource.password=siwar00siwar
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Step 3: Build the Services

From the workspace root directory:

```bash
# Build Admin Service
cd admin-service
mvn clean install

# Build User Service
cd ../user-service
mvn clean install
```

## Step 4: Run the Services

**Terminal 1 - Admin Service:**
```bash
cd admin-service
mvn spring-boot:run
```
Expected output: `Started AdminServiceApplication in X seconds`

**Terminal 2 - User Service:**
```bash
cd user-service
mvn spring-boot:run
```
Expected output: `Started UserServiceApplication in X seconds`

Both services should start successfully:
- Admin Service: http://localhost:8080
- User Service: http://localhost:8081

## Step 5: Verify Services Are Running

### Check Service Health

**Admin Service:**
```bash
curl http://localhost:8080/actuator/health
```

**User Service:**
```bash
curl http://localhost:8081/actuator/health
```

## Step 6: Test Inter-Service Communication

### Create a User (User Service)
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "active": true
  }'
```

### Create an Admin (Admin Service)
```bash
curl -X POST http://localhost:8080/api/admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@example.com",
    "role": "SUPER_ADMIN"
  }'
```

### Get All Users (from Admin Service)
```bash
curl http://localhost:8080/api/admin/users
```
This endpoint on the Admin Service calls the User Service REST API internally.

### Deactivate a User
```bash
curl -X POST http://localhost:8081/api/users/{userId}/deactivate
```

This will deactivate the user in the database.

## Troubleshooting

### Issue: Port Already in Use
```bash
# Change port in application.properties
# Admin Service: server.port=8082
# User Service: server.port=8081
```

### Issue: PostgreSQL Connection Failed
- Verify PostgreSQL is running: `psql -U postgres`
- Check credentials in `application.properties` match your setup
- Default: username=`postgres`, password=`siwar00siwar`, database=`users`

### Issue: Admin Service Can't Reach User Service
- Ensure both services are running
- Verify `user.service.url=http://localhost:8081` in admin-service properties
- Check firewall settings

## Service Architecture

```
┌─────────────────────────────────────────┐
│         Admin Service (8082)            │
│  ┌─────────────────────────────────┐   │
│  │  AdminController                │   │
│  │  AdminService                   │   │
│  │  UserServiceClient (REST)       │───┼─── User Service (8081)
│  │  AdminRepository                │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
         │
         └──── PostgreSQL (localhost:5432)
         
┌─────────────────────────────────────────┐
│         User Service (8081)             │
│  ┌─────────────────────────────────┐   │
│  │  UserController                 │   │
│  │  UserService                    │   │
│  │  UserRepository                 │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
         │
         └──── PostgreSQL (localhost:5432)
```

## Database Schema

The services use Hibernate auto-schema generation (`spring.jpa.hibernate.ddl-auto=update`).

Tables created automatically:
- `admin` - Admin user records
- `user` - User records

## API Endpoints

### Admin Service
- `POST /api/admin` - Create admin
- `GET /api/admin/{id}` - Get admin by ID
- `GET /api/admin` - Get all admins
- `PUT /api/admin/{id}` - Update admin
- `DELETE /api/admin/{id}` - Delete admin
- `GET /api/admin/users` - Get all users (calls User Service)
- `POST /api/admin/users/{id}/deactivate?reason=<reason>` - Deactivate user via admin

### User Service
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users` - Get all users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/{id}/deactivate` - Deactivate user

## Quick Start (Docker - PostgreSQL Only)

Create `docker-compose.yml` in the workspace root:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: siwar00siwar
      POSTGRES_DB: users
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

Start with: `docker-compose up -d`

## Project Information

- **Java Version:** 17
- **Spring Boot Version:** 3.1.5
- **Database:** PostgreSQL 15
- **Communication:** Synchronous REST APIs (no message queues)
- **ORM:** Spring Data JPA with Hibernate


All services now use synchronous REST communication for inter-service calls.


