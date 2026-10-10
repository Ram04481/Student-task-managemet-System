# Student Task Management System

A full-stack student task management application built using Spring Boot, React, TypeScript, and MySQL.

## Features

### Backend (Implemented & Verified)
- User registration with BCrypt password hashing
- User login with JWT authentication
- Current user profile endpoint
- JWT token generation, validation, and expiration handling
- Stateless session management with Spring Security
- Task CRUD operations (create, read, update, delete)
- Task status (PENDING, IN_PROGRESS, COMPLETED)
- Task priority (LOW, MEDIUM, HIGH)
- Task due dates
- Dashboard with task statistics (total, pending, in-progress, completed)
- Ownership enforcement - users can only access their own tasks
- Global exception handling with consistent error responses
- CORS configured for frontend origin
- MySQL persistence with Hibernate/JPA

### Frontend (Implemented & Verified)
- React 19 + TypeScript + Vite
- User registration page with validation
- User login page with validation
- Protected routes with authentication context
- Dashboard with summary cards
- Task list with status/priority badges
- Create task form
- Edit task form
- View task details
- Delete task with confirmation
- Profile page with logout
- Axios client with JWT interceptor
- Automatic token refresh on page load
- Responsive design with Tailwind CSS

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.2
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- JWT (io.jsonwebtoken:jjwt-api/impl/jackson 0.12.3)
- MySQL (mysql-connector-j)
- Maven
- Lombok
- H2 Database (for testing)

### Frontend
- React 19
- TypeScript
- Vite 8
- React Router 7
- Axios
- Tailwind CSS (via CDN)
- oxlint

### Database
- MySQL 8.0+ (production)
- H2 (testing)

## Project Structure

```
Student Task Management System/
│
├── Backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── studenttask/
│   │   │   │           ├── StudentTaskApplication.java
│   │   │   │           ├── config/
│   │   │   │           │   ├── JwtConfig.java
│   │   │   │           │   └── SecurityConfig.java
│   │   │   │           ├── controller/
│   │   │   │           │   ├── AuthController.java
│   │   │   │           │   ├── HealthController.java
│   │   │   │           │   └── TaskController.java
│   │   │   │           ├── dto/
│   │   │   │           │   ├── AuthResponse.java
│   │   │   │           │   ├── DashboardResponse.java
│   │   │   │           │   ├── LoginRequest.java
│   │   │   │           │   ├── RegisterRequest.java
│   │   │   │           │   ├── TaskRequest.java
│   │   │   │           │   └── TaskResponse.java
│   │   │   │           ├── entity/
│   │   │   │           │   ├── Task.java
│   │   │   │           │   ├── TaskPriority.java
│   │   │   │           │   ├── TaskStatus.java
│   │   │   │           │   └── User.java
│   │   │   │           ├── exception/
│   │   │   │           │   └── GlobalExceptionHandler.java
│   │   │   │           ├── repository/
│   │   │   │           │   ├── TaskRepository.java
│   │   │   │           │   └── UserRepository.java
│   │   │   │           ├── security/
│   │   │   │           │   ├── CustomUserDetailsService.java
│   │   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │   │           │   └── JwtService.java
│   │   │   │           └── service/
│   │   │   │               ├── AuthService.java
│   │   │   │               └── TaskService.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/studenttask/
│   │       │       ├── config/
│   │       │       │   └── TestSecurityConfig.java
│   │       │       ├── repository/
│   │       │       │   └── TaskRepositoryTest.java
│   │       │       ├── security/
│   │       │       │   ├── CustomUserDetailsServiceTest.java
│   │       │       │   ├── JwtAuthenticationFilterTest.java
│   │       │       │   └── JwtServiceTest.java
│   │       │       └── service/
│   │       │           ├── AuthServiceTest.java
│   │       │           └── TaskServiceTest.java
│   │       └── resources/
│   │           ├── application-test.properties
│   │           └── schema.sql
│   ├── pom.xml
│   └── .env (ignored)
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── client.ts
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── Navbar.tsx
│   │   │   ├── ProtectedRoute.tsx
│   │   │   └── Sidebar.tsx
│   │   ├── context/
│   │   │   └── AuthContext.tsx
│   │   ├── hooks/
│   │   ├── pages/
│   │   │   ├── AddTask.tsx
│   │   │   ├── Dashboard.tsx
│   │   │   ├── EditTask.tsx
│   │   │   ├── Login.tsx
│   │   │   ├── Profile.tsx
│   │   │   ├── Register.tsx
│   │   │   └── Tasks.tsx
│   │   ├── types/
│   │   │   └── index.ts
│   │   ├── utils/
│   │   ├── App.tsx
│   │   ├── App.css
│   │   ├── main.tsx
│   │   └── index.css
│   ├── public/
│   ├── .env.example
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.json
│   ├── tsconfig.node.json
│   ├── vite.config.js
│   └── index.html
│
├── .gitignore
├── .env.example
└── README.md
```

## Backend Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+

### Database Configuration

1. Install MySQL and start the service
2. Create a database:
   ```sql
   CREATE DATABASE student_task_db;
   ```

### Configuration

1. Copy `.env.example` to a `.env` file in the Backend directory and configure your credentials:
   ```bash
   cd Backend
   cp ../.env.example .env
   # Edit .env with your database credentials and JWT secret
   ```

2. The `application.properties` uses environment variables:
   ```properties
   spring.datasource.url=${DB_URL}
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   
   jwt.secret=${JWT_SECRET}
   jwt.expiration=${JWT_EXPIRATION}
   
   server.port=${SERVER_PORT}
   cors.allowed-origins=${FRONTEND_URL}
   ```

### Build and Run

```bash
cd Backend
mvn clean test          # Run tests
mvn spring-boot:run     # Start development server
```

The backend will start on `http://localhost:8080`

### Verify Backend

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "message": "Student Task Management API is running"
}
```

## Frontend Setup

### Prerequisites
- Node.js 18+

### Install and Run

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### Build for Production

```bash
cd frontend
npm run build
```

Output will be in `dist/` directory.

## Environment Variables

The project uses environment variables for configuration. Copy `.env.example` to `.env` (in Backend directory) and fill in your values:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL JDBC connection URL | `jdbc:mysql://localhost:3306/student_task_db` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `your_secure_password` |
| `JWT_SECRET` | Secret key for JWT signing (min 256 bits) | `your_256_bit_secret_key_here` |
| `JWT_EXPIRATION` | Token expiration in milliseconds | `86400000` (24 hours) |
| `SERVER_PORT` | Backend server port | `8080` |
| `FRONTEND_URL` | Frontend URL for CORS | `http://localhost:5173` |

**Frontend (.env.example):**
| Variable | Description | Example |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Backend API base URL | `http://localhost:8080/api` |

**⚠️ Never commit `.env` files or real secrets to version control!**

## API Endpoints

### Health Check
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check endpoint |

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/auth/me` | Get current user profile |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create new task |
| GET | `/api/tasks` | Get all tasks for authenticated user |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/dashboard` | Get dashboard statistics |

### Request/Response Examples

**Register Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

**Login Request:**
```json
{
  "email": "john@example.com",
  "password": "Password123"
}
```

**Auth Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

**Task Request:**
```json
{
  "title": "Complete assignment",
  "description": "Finish the Java project",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2026-12-31T23:59:00"
}
```

**Task Response:**
```json
{
  "id": 1,
  "title": "Complete assignment",
  "description": "Finish the Java project",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2026-12-31T23:59:00",
  "createdAt": "2026-10-10T10:00:00",
  "updatedAt": "2026-10-10T10:00:00"
}
```

**Dashboard Response:**
```json
{
  "totalTasks": 10,
  "pendingTasks": 4,
  "inProgressTasks": 3,
  "completedTasks": 3
}
```

## Security

- Passwords are hashed with BCrypt (strength 10)
- JWT tokens signed with HS512 algorithm (256+ bit secret)
- Stateless authentication - no server-side sessions
- CORS restricted to configured frontend origin
- Ownership enforced at service layer
- No sensitive data in logs or API responses
- All secrets via environment variables

## Testing

### Backend Tests
```bash
cd Backend
mvn test
```

**Test Results:**
```
Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Test Class | Tests |
|------------|-------|
| TaskRepositoryTest | 9 |
| CustomUserDetailsServiceTest | 2 |
| JwtAuthenticationFilterTest | 8 |
| JwtServiceTest | 10 |
| AuthServiceTest | 8 |
| TaskServiceTest | 11 |

### Frontend Checks
```bash
cd frontend
npm run lint    # Linting (warnings only)
npm run build   # Production build
```

## Development

### Running Tests

**Backend:**
```bash
cd Backend
mvn test
```

**Frontend:**
```bash
cd frontend
npm run lint
npm run build
```

### Building for Production

**Backend:**
```bash
cd Backend
mvn clean package -DskipTests
# JAR file will be in target/
```

**Frontend:**
```bash
cd frontend
npm run build
# Output in dist/
```

## Current Status

| Component | Status |
|-----------|--------|
| Backend - Project Structure | ✅ Complete |
| Backend - User Entity & Repository | ✅ Complete |
| Backend - Health Endpoint | ✅ Complete |
| Backend - Spring Security Config | ✅ Complete |
| Backend - JWT Authentication | ✅ Complete |
| Backend - Auth Controllers | ✅ Complete |
| Backend - Task Entity & Repository | ✅ Complete |
| Backend - Task Controllers & Services | ✅ Complete |
| Backend - Global Exception Handling | ✅ Complete |
| Backend - Automated Tests (48 passing) | ✅ Complete |
| Frontend - Project Scaffold | ✅ Complete |
| Frontend - Pages & Components | ✅ Complete |
| Frontend - API Integration | ✅ Complete |
| Frontend - Authentication Flow | ✅ Complete |
| Frontend - Task CRUD UI | ✅ Complete |
| Database - Schema Creation | ✅ Complete |
| End-to-End Verification | ✅ Complete |

## Troubleshooting

### Backend won't start - JWT Secret error
Ensure `JWT_SECRET` in `.env` is at least 256 bits (32 characters) for HS512 algorithm.

### Database connection failed
- Verify MySQL is running
- Check `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in `.env`
- Ensure `student_task_db` database exists

### CORS errors in frontend
- Verify `FRONTEND_URL` in backend `.env` matches frontend URL
- Check backend is running on port 8080
- Check frontend is running on port 5173

### Frontend build fails
- Run `npm install` to ensure dependencies
- Check Node.js version (18+ required)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Contact

Project Link: [https://github.com/Ram04481/student-task-management-system](https://github.com/Ram04481/student-task-management-system)