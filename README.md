# Tutorly - Tutoring Marketplace Platform (Spring Boot)

A full-stack tutoring marketplace platform built with **Spring Boot** (backend) and **React** (frontend), similar to Preply but not limited to languages.

## 🚀 Features

### User Roles
- **Student**: Browse tutors, book lessons, leave reviews
- **Tutor**: Create profile, manage availability, receive bookings
- **Admin**: Manage users, verify tutors, view platform statistics

### Core Functionality
- **JWT Authentication** with role-based access control
- **Tutor Profiles** with ratings, reviews, subjects, and pricing
- **Advanced Search & Filtering** by subject, price range, and rating
- **Booking System** with scheduling and payment tracking
- **Review System** with automatic tutor rating calculation
- **Admin Panel** for platform management
- **Responsive Design** with dark/light mode support

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven

### Frontend
- **Framework**: React with Vite
- **Styling**: TailwindCSS
- **HTTP Client**: Axios
- **Icons**: Lucide React

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 14+**
- **Node.js 18+** (for frontend)

## 🔧 Installation & Setup

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE tutorly;
```

### 2. Backend Setup

```bash
cd tutorly

# Update application.properties with your database credentials
# File: src/main/resources/application.properties

# Build and run the application
./mvnw clean install
./mvnw spring-boot:run
```

The backend API will be available at `http://localhost:8080`

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:5173`

## 🔑 Demo Accounts

The application automatically seeds demo data on first run:

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@tutorly.com | admin123 |
| **Student** | student1@example.com | password123 |
| **Tutor** | john.smith@tutorly.com | password123 |

**8 Demo Tutors** are available across all subjects with verified profiles.

## 📁 Project Structure

```
tutorly/
├── src/main/java/com/main/tutorly/
│   ├── config/          # Security, CORS, Data initialization
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── repository/      # Data access layer
│   ├── security/        # JWT utilities and filters
│   └── service/         # Business logic
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user (requires auth)

### Tutors
- `GET /api/tutors` - Get all tutors (with filters)
- `GET /api/tutors/{id}` - Get tutor details
- `PUT /api/tutors/profile` - Update tutor profile (requires auth)

### Bookings
- `POST /api/bookings` - Create booking (requires auth)
- `GET /api/bookings/my-bookings` - Get user's bookings (requires auth)
- `PATCH /api/bookings/{id}/status` - Update booking status
- `PATCH /api/bookings/{id}/payment` - Update payment status

### Reviews
- `POST /api/reviews` - Create review (requires auth)
- `GET /api/reviews/tutor/{tutorId}` - Get tutor reviews

### Subjects
- `GET /api/subjects` - Get all subjects
- `POST /api/subjects` - Create subject (admin only)
- `DELETE /api/subjects/{id}` - Delete subject (admin only)

### Admin
- `GET /api/admin/users` - Get all users (admin only)
- `DELETE /api/admin/users/{id}` - Delete user (admin only)
- `GET /api/admin/stats` - Get platform statistics (admin only)
- `PATCH /api/admin/tutors/{id}/verify` - Verify tutor (admin only)
- `PATCH /api/admin/tutors/{id}/active` - Set tutor active status (admin only)

## 🎨 Available Subjects

- Programming 💻
- Mathematics 📐
- Physics ⚛️
- English 📚
- Business 💼
- Design 🎨
- Cybersecurity 🔒

## ⚙️ Configuration

### Database Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorly
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JWT Configuration

```properties
jwt.secret=your_secret_key_here
jwt.expiration=86400000
```

### CORS Configuration

```properties
cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

## 🚀 Building for Production

```bash
# Build backend JAR
./mvnw clean package

# Run the JAR
java -jar target/tutorly-0.0.1-SNAPSHOT.jar

# Build frontend
cd frontend
npm run build
```

## 📝 Development Notes

- **Automatic Schema Creation**: JPA is configured with `ddl-auto=update` for development
- **Data Initialization**: Demo data is automatically loaded on first run
- **Password Encryption**: BCrypt is used for secure password hashing
- **JWT Expiration**: Tokens expire after 24 hours by default

## 🐛 Troubleshooting

### Backend won't start
- Ensure PostgreSQL is running
- Check database credentials in `application.properties`
- Verify Java 17 is installed: `java -version`

### Database connection errors
- Verify PostgreSQL service is running
- Check database exists: `psql -U postgres -l`
- Ensure user has proper permissions

### Port already in use
- Change port in `application.properties`: `server.port=8081`

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

---

**Built with ❤️ using Spring Boot and React**
