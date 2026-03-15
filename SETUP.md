# Tutorly - Quick Setup Guide (Spring Boot Version)

## Prerequisites

- **Java 17** or higher - [Download](https://adoptium.net/)
- **Maven 3.6+** - Usually comes with Java
- **PostgreSQL 14+** - [Download](https://www.postgresql.org/download/)
- **Node.js 18+** - [Download](https://nodejs.org/) (for frontend)

## Quick Start

### Step 1: Create PostgreSQL Database

```bash
# Using psql command line
psql -U postgres
CREATE DATABASE tutorly;
\q
```

Or use pgAdmin/DBeaver to create a database named `tutorly`.

### Step 2: Configure Database Connection

Edit `tutorly/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorly
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

### Step 3: Run Spring Boot Backend

```bash
cd tutorly

# On Windows
mvnw.cmd spring-boot:run

# On Mac/Linux
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

**✅ Demo data is automatically loaded on first run!**

### Step 4: Run React Frontend

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### Step 5: Access the Application

Open your browser and navigate to `http://localhost:5173`

## Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@tutorly.com | admin123 |
| **Student** | student1@example.com | password123 |
| **Tutor** | john.smith@tutorly.com | password123 |

## API Testing

The REST API is available at `http://localhost:8080/api`

Example: Get all tutors
```bash
curl http://localhost:8080/api/tutors
```

## Features to Explore

1. **Browse Tutors** - View all available tutors with filters
2. **Register/Login** - Create account as student or tutor
3. **Book Lessons** - Schedule sessions with tutors
4. **Leave Reviews** - Rate and review completed lessons
5. **Admin Panel** - Manage users and verify tutors (admin only)
6. **Dark Mode** - Toggle theme in the UI

## Troubleshooting

### "Port 8080 already in use"
Change the port in `application.properties`:
```properties
server.port=8081
```

### Database connection failed
- Ensure PostgreSQL is running
- Check username/password in `application.properties`
- Verify database `tutorly` exists

### Maven build fails
```bash
# Clean and rebuild
./mvnw clean install
```

### Frontend won't start
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## Project Structure

```
tutorly/
├── src/main/java/          # Java source code
│   └── com/main/tutorly/
│       ├── config/         # Configuration classes
│       ├── controller/     # REST controllers
│       ├── entity/         # JPA entities
│       ├── repository/     # Data repositories
│       ├── service/        # Business logic
│       └── security/       # JWT & security
├── src/main/resources/     # Configuration files
│   └── application.properties
├── pom.xml                 # Maven dependencies
└── frontend/               # React application

```

## Next Steps

- Customize the UI in `frontend/src`
- Add new API endpoints in `controller/`
- Modify database schema in `entity/`
- Configure production database settings

Enjoy using Tutorly! 🎓
