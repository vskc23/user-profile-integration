# User Profile Integration

This project is a fully functional Spring Boot application that provides REST endpoints for user registration, image upload (via Imgur), and secure authentication. It is built with a three-layer architecture (Controller, Service, Repository) and includes production-grade logging, unit tests, and exception handling.

## 1. Running the Application

### Clone the Repository
Clone the project from GitHub.

### Prerequisites
- Java 17
- Maven

### Dependencies
All required dependencies are defined in the `pom.xml`. Run the following command to download them and build the project:

```bash
mvn clean install
```

### Environment Variables
Create a `.env` file (not checked into version control) in the project root (next to the `pom.xml`) with the following content:

```ini
IMGUR_CLIENT_ID=your-imgur-client-id
IMGUR_CLIENT_SECRET=your-imgur-client-secret
IMGUR_UPLOAD_URL=https://api.imgur.com/3/upload
IMGUR_DELETE_URL=https://api.imgur.com/3/image/
```

The `.env` file is excluded via `.gitignore`. Replace the placeholders with your actual credentials.

### Running the Application
Start the application with:

```bash
mvn spring-boot:run
```

For more detailed output, you may add `-X` to your Maven command.

### Swagger UI
Once the application is running, you can access Swagger at:

```bash
http://localhost:8080/swagger-ui/index.html
```

## 2. Project Overview
This project meets the provided requirements and follows industry best practices:

### Development Environment:
Developed using IntelliJ IDEA with Git for version control.

### Architecture:
Implements a three-layer architecture:
- **Controller**: Exposes REST endpoints.
- **Service**: Contains business logic, including secure authentication and external Imgur API integration.
- **Repository**: Manages data persistence using JPA with an in-memory H2 database.

### Data Transfer Objects:
Uses request and response DTOs to ensure only relevant data is exchanged, with sensitive details (such as passwords) excluded from responses.

### Logging:
Logging is implemented using Log4j2, providing production-grade logs at key operations (e.g., user registration, image upload, image deletion).

### Security:
Secure authentication has been implemented and tested.
- User passwords are securely encoded using BCrypt.
- Endpoints requiring authorization enforce security.

### Environment Configuration:
All sensitive properties (e.g., Imgur client credentials) are externalized into environment variables using a `.env` file.

### Testing & Exception Handling:
- Unit tests cover core functionality and error scenarios.
- Added important exception handling such as USerNotFoundException.
