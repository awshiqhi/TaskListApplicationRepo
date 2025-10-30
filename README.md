# Spring Boot Task Management Application

A full-stack web application for user registration, authentication, and task management built with Spring Boot, Spring Security, and Thymeleaf.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Running the Application](#running-the-application)
- [Packaging and Deployment](#packaging-and-deployment)
- [Default Credentials](#default-credentials)
- [API Endpoints](#api-endpoints)
- [Database Access](#database-access)

---

## Architecture Overview

This application follows a **three-tier MVC (Model-View-Controller) architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Thymeleaf   │  │     CSS      │  │  JavaScript  │      │
│  │  Templates   │  │   Styling    │  │  (Client)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                    Controllers                        │   │
│  │  (ContentController, TaskController, etc.)           │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                  Business Logic                       │   │
│  │        (Services: TaskService, UserService)          │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Security & Exception Handling            │   │
│  │   (SecurityConfig, GlobalExceptionHandler)           │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              JPA Entities & Repositories              │   │
│  │      (MyAppUser, Task, Repositories)                 │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              H2 In-Memory Database                    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.3.0 | Application framework for rapid development |
| **Spring Web** | - | RESTful API and MVC support |
| **Spring Data JPA** | - | Database abstraction and ORM |
| **Spring Security** | - | Authentication and authorization |
| **Hibernate** | - | ORM implementation |
| **Lombok** | - | Reduces boilerplate code (getters, setters, constructors) |

**Why Spring Boot?**
- **Rapid Development**: Auto-configuration reduces setup time
- **Production-Ready**: Built-in metrics, health checks, and externalized configuration
- **Embedded Server**: No need for external Tomcat/Jetty installation
- **Microservices Ready**: Easily scalable and cloud-deployable

### Frontend Technologies

| Technology | Purpose |
|-----------|---------|
| **Thymeleaf** | Server-side template engine for dynamic HTML rendering |
| **HTML5** | Structure and semantic markup |
| **CSS3** | Styling and responsive design |
| **JavaScript (Vanilla)** | Client-side interactivity and AJAX calls |

**Why Thymeleaf?**
- **Natural Templates**: Works with or without server rendering
- **Spring Integration**: Seamless integration with Spring Security
- **No Build Process**: Direct template editing and hot-reload support

### Database

| Technology | Purpose |
|-----------|---------|
| **H2 Database** | In-memory relational database |
| **Hibernate/JPA** | Object-relational mapping |

**Why H2?**
- **Zero Configuration**: No installation required
- **Development Speed**: Perfect for prototyping and testing
- **Web Console**: Built-in admin interface at `/h2-console`
- **Production Alternative**: Easily switchable to PostgreSQL/MySQL

### Build & Dependency Management

| Technology | Purpose |
|-----------|---------|
| **Maven** | Build automation and dependency management |
| **Maven Wrapper** | Ensures consistent Maven version across environments |

---

## Project Structure

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/tasklistapp/
│   │   │   ├── Config/
│   │   │   │   └── DataInitializer.java          # Default user initialization
│   │   │   ├── Controller/
│   │   │   │   ├── ContentController.java        # Page routing
│   │   │   │   ├── RegistrationController.java   # User registration
│   │   │   │   └── TaskController.java           # Task CRUD operations
│   │   │   ├── Model/
│   │   │   │   ├── MyAppUser.java                # User entity
│   │   │   │   ├── MyAppUserRepository.java      # User data access
│   │   │   │   ├── MyAppUserService.java         # User business logic
│   │   │   │   ├── Task.java                     # Task entity
│   │   │   │   └── TaskRepository.java           # Task data access
│   │   │   ├── Security/
│   │   │   │   └── SecurityConfig.java           # Spring Security configuration
│   │   │   ├── service/
│   │   │   │   └── TaskService.java              # Task business logic
│   │   │   ├── validation/                       # Input validation
│   │   │   │   ├── PasswordValidator.java        # Password security validator
│   │   │   │   ├── ValidPassword.java            # Custom validation annotation
│   │   │   │   └── InputSanitizer.java           # SQL injection & XSS prevention
│   │   │   ├── dto/
│   │   │   │   ├── ErrorResponse.java            # Standardized error response
│   │   │   │   └── UserRegistrationRequest.java  # Registration DTO with validation
│   │   │   ├── exception/                        # Custom exceptions
│   │   │   │   ├── GlobalExceptionHandler.java   # Centralized error handling
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── InvalidRequestException.java
│   │   │   └── TaskListAppApplication.java              # Main application class
│   │   └── resources/
│   │       ├── templates/                        # Thymeleaf HTML templates
│   │       │   ├── login.html
│   │       │   ├── signup.html
│   │       │   └── tasklist.html
│   │       ├── static/                           # Static assets
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   └── js/
│   │       │       ├── signup.js
│   │       │       └── tasklist.js
│   │       ├── public/error/
│   │       │   └── 404.html
│   │       └── application.yml                   # Application configuration
│   └── test/
│       └── java/com/example/tasklistapp/
│           ├── Controller/
│           │   ├── RegistrationControllerTest.java
│           │   └── TaskControllerTest.java
│           ├── service/
│           │   └── TaskServiceTest.java
│           ├── validation/
│           │   ├── PasswordValidatorTest.java
│           │   └── InputSanitizerTest.java
│           └── TaskListAppApplicationTests.java
├── target/                                       # Compiled classes and JAR
├── mvnw                                          # Maven wrapper (Unix)
├── mvnw.cmd                                      # Maven wrapper (Windows)
├── pom.xml                                       # Maven configuration
└── README.md                                     # This file
```

---

## Features

### ✅ User Management
- User registration with validation
- Secure password encryption (BCrypt)
- Form-based authentication
- Input sanitization (SQL injection & XSS prevention)
- Password validation (minimum 3 characters, security checks)
- Auto-verified user accounts
- Default admin user (`admin`/`admin123`)

### ✅ Task Management
- Create, read, update, delete (CRUD) tasks
- Task status tracking (TO_DO, IN_PROGRESS, DONE)
- User-specific task isolation
- Task filtering by status
- Persistent storage with H2 database

### ✅ Security Features
- Spring Security integration
- Password encryption with BCrypt
- Input sanitization against SQL injection and XSS attacks
- Multi-layer validation (frontend + backend)
- Session management
- CSRF protection
- Authorization checks
- Custom login/logout

### ✅ Exception Handling
- Global exception handler
- Custom exception types
- Standardized error responses
- Proper HTTP status codes
- Request logging

### ✅ Input Validation
- Password validation (min 3 characters, max 128 characters)
- SQL injection prevention
- XSS attack prevention
- Multi-layer validation (client-side + server-side)
- Custom Jakarta Bean Validation annotations
- Comprehensive test coverage (71 tests)

---

## Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK) 17 or higher**
  - Download: https://adoptium.net/
  - Verify: `java -version`

- **Maven 3.6+** (Optional - Maven Wrapper included)
  - Verify: `mvn -version`

- **Git** (for cloning the repository)
  - Download: https://git-scm.com/

---

## Running the Application

### Option 1: From Your IDE (IntelliJ IDEA / Eclipse / VS Code)

1. **Import the Project**
   - Open your IDE
   - Select **Import Project** or **Open**
   - Navigate to the project directory
   - Select `pom.xml` as the project descriptor

2. **Wait for Dependencies to Download**
   - Maven will automatically download all dependencies

3. **Run the Application**
   - Locate `TaskListAppApplication.java`
   - Right-click and select **Run 'TaskListAppApplication'**
   - Or click the green play button next to the main method

4. **Access the Application**
   - Open browser: http://localhost:8080
   - Login page: http://localhost:8080/req/login
   - Signup page: http://localhost:8080/req/signup

### Option 2: From Command Line (Using Maven Wrapper)

**Windows:**
```bash
cd c:\Users\sheme\Downloads\springbootBackend-main\springbootBackend-main\demo
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
cd /path/to/demo
./mvnw spring-boot:run
```

**Using Installed Maven:**
```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

### Option 3: Running Tests

```bash
# Windows
.\mvnw.cmd test

# Linux/Mac
./mvnw test
```

---

## Packaging and Deployment

### Build Executable JAR

The application is already configured to build as an executable JAR with embedded Tomcat.

**Build Command:**

```bash
# Windows
.\mvnw.cmd clean package

# Linux/Mac
./mvnw clean package
```

This creates a JAR file in the `target/` directory:
- `target/tasklistapp-0.0.1-SNAPSHOT.jar`

### Run the Packaged JAR

```bash
# Navigate to target directory
cd target

# Run the JAR
java -jar tasklistapp-0.0.1-SNAPSHOT.jar
```

**Access the application:** http://localhost:8080

### Production Deployment Options

#### 1️⃣ **Deploy to Linux Server**

```bash
# Upload JAR to server
scp target/tasklistapp-0.0.1-SNAPSHOT.jar user@server:/opt/app/

# SSH into server
ssh user@server

# Run application
cd /opt/app
nohup java -jar tasklistapp-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

#### 2️⃣ **Run as System Service (systemd)**

Create `/etc/systemd/system/tasklistapp.service`:

```ini
[Unit]
Description=Spring Boot TaskListApp Application
After=syslog.target

[Service]
User=appuser
ExecStart=/usr/bin/java -jar /opt/app/tasklistapp-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable tasklistapp
sudo systemctl start tasklistapp
sudo systemctl status tasklistapp
```

#### 3️⃣ **Docker Deployment**

Create `Dockerfile` in project root:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/tasklistapp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t spring-boot-tasklistapp .
docker run -p 8080:8080 spring-boot-tasklistapp
```

#### 4️⃣ **Cloud Platforms**

- **Heroku**: `git push heroku main`
- **AWS Elastic Beanstalk**: Upload JAR via console
- **Google Cloud Run**: Deploy containerized app
- **Azure App Service**: Deploy JAR directly

### Configuration for Production

Create `application-prod.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: dbuser
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  h2:
    console:
      enabled: false

logging:
  level:
    root: INFO
    com.example.tasklistapp: DEBUG
```

Run with production profile:
```bash
java -jar tasklistapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Default Credentials

A default admin user is automatically created on first startup:

| Field | Value |
|-------|-------|
| **Username** | `admin` |
| **Password** | `admin` |
| **Email** | `admin@demo.com` |

⚠️ **Security Note**: Change this password in production environments!

---

## API Endpoints

### Authentication
- `GET /req/login` - Login page
- `GET /req/signup` - Signup page
- `POST /req/signup` - Create new user
- `POST /logout` - Logout user

### Task Management
- `GET /tasklist` - Task management page
- `GET /tasks` - Get all tasks for logged-in user
- `GET /tasks/{id}` - Get specific task
- `POST /tasks` - Create new task
- `PUT /tasks/{id}` - Update task
- `DELETE /tasks/{id}` - Delete task

### Database Console
- `GET /h2-console` - H2 database web console

---

## Database Access

### H2 Console

While the application is running:

1. Navigate to: http://localhost:8080/h2-console
2. Use these settings:
   - **Driver Class**: `org.h2.Driver`
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: *(leave empty)*
3. Click **Connect**

### Database Schema

**Users Table**: `MY_APP_USER`
- `id` (BIGINT, PK)
- `username` (VARCHAR)
- `email` (VARCHAR)
- `password` (VARCHAR)
- `is_verified` (BOOLEAN)
- `reset_token` (VARCHAR)

**Tasks Table**: `TASK`
- `id` (VARCHAR, PK)
- `short_description` (VARCHAR)
- `long_description` (TEXT)
- `status` (VARCHAR)
- `created_at` (TIMESTAMP)
- `user_id` (BIGINT, FK)

---

## Exception Handling

The application includes comprehensive exception handling:

- **ResourceNotFoundException** (404): Resource not found
- **UnauthorizedException** (403): Access denied
- **DuplicateResourceException** (409): Duplicate entries
- **InvalidRequestException** (400): Bad requests

All errors return standardized JSON responses:

```json
{
  "timestamp": "2025-10-30T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: '123'",
  "path": "/tasks/123"
}
```

---

## Troubleshooting

### Port Already in Use
```bash
# Windows - Find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Build Failures
```bash
# Clean and rebuild
.\mvnw.cmd clean install -U
```

### Java Version Issues
```bash
# Check Java version
java -version

# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk17
```

---

## Development

### Hot Reload

The application includes Spring Boot DevTools for automatic restart on code changes.

### Adding New Dependencies

Edit `pom.xml` and run:
```bash
.\mvnw.cmd clean install
```

---

## License

This project is for educational purposes.

---

## Contact & Support

For issues or questions:
- Check application logs in console
- Review H2 console for database issues
- Examine browser console for frontend errors

**Application Version**: 0.0.1-SNAPSHOT  
**Last Updated**: 2025-10-30
