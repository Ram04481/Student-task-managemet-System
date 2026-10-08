# Student Task Management System

A full-stack student task management application built using Spring Boot, React and MySQL.

## Features

### Currently Implemented (Backend)
- User entity with JPA/Hibernate mapping
- User repository with Spring Data JPA
- Health check endpoint (`GET /api/health`)
- Basic Spring Security configuration (stateless, CSRF disabled)
- MySQL database configuration via environment variables
- JWT dependencies configured (implementation in progress)

### Currently Implemented (Frontend)
- React 18 + Vite project scaffold
- Basic project structure ready for development

### Planned Features (Under Development)
- User registration
- User login
- JWT authentication
- Student dashboard
- Task CRUD operations (create, read, update, delete)
- Task priority, status, due dates
- Search and filter tasks
- REST API endpoints for tasks

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.2
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (io.jsonwebtoken:jjwt-api/impl/jackson)
- MySQL (mysql-connector-j)
- Maven
- Lombok

### Frontend
- React 18
- Vite 6
- JavaScript (ES6+)

### Database
- MySQL 8.0+

### Tools
- Git
- GitHub
- Postman (for API testing)

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
│   │   │   │           │   └── SecurityConfig.java
│   │   │   │           ├── controller/
│   │   │   │           │   └── HealthController.java
│   │   │   │           ├── entity/
│   │   │   │           │   └── User.java
│   │   │   │           ├── repository/
│   │   │   │           │   └── UserRepository.java
│   │   │   │           ├── security/
│   │   │   │           ├── service/
│   │   │   │           ├── dto/
│   │   │   │           └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   └── target/ (ignored)
│
├── Frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── main.jsx
│   │   └── index.css
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   ├── node_modules/ (ignored)
│   └── dist/ (ignored)
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
   ```

### Build and Run

```bash
cd Backend
mvn clean install
mvn spring-boot:run
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
cd Frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### Build for Production

```bash
cd Frontend
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

**⚠️ Never commit `.env` files or real secrets to version control!**

## API Endpoints

### Health Check
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check endpoint |

### Authentication (Planned)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/auth/me` | Get current user profile |

### Tasks (Planned)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create new task |
| GET | `/api/tasks` | Get all tasks (with search/filter) |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| PATCH | `/api/tasks/{id}/status` | Update task status |

## Current Status

| Component | Status |
|-----------|--------|
| Backend - Project Structure | ✅ Complete |
| Backend - User Entity & Repository | ✅ Complete |
| Backend - Health Endpoint | ✅ Complete |
| Backend - Spring Security Config (Basic) | ✅ Complete |
| Backend - JWT Authentication | 🚧 In Progress |
| Backend - Auth Controllers (Register/Login) | ⏳ Not Started |
| Backend - Task Entity & Repository | ⏳ Not Started |
| Backend - Task Controllers & Services | ⏳ Not Started |
| Frontend - Project Scaffold | ✅ Complete |
| Frontend - Pages & Components | ⏳ Not Started |
| Frontend - API Integration | ⏳ Not Started |
| Database - Schema Creation | ✅ Auto (Hibernate) |

## Development

### Running Tests

**Backend:**
```bash
cd Backend
mvn test
```

**Frontend:**
```bash
cd Frontend
npm test
```

### Building for Production

**Backend:**
```bash
cd Backend
mvn clean package
# JAR file will be in target/
```

**Frontend:**
```bash
cd Frontend
npm run build
# Output in dist/
```

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