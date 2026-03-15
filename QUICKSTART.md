# Tutorly - Quick Start Guide

Get Tutorly running in 5 minutes!

## 🚀 Quick Start (Local Development)

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 15+

### Step 1: Database Setup (1 minute)

```bash
# Create database
psql -U postgres
CREATE DATABASE tutorly;
\q
```

### Step 2: Backend Setup (2 minutes)

```bash
# Navigate to backend
cd tutorly

# Run Spring Boot (Windows)
mvnw.cmd spring-boot:run

# Run Spring Boot (Mac/Linux)
./mvnw spring-boot:run
```

Backend starts on: **http://localhost:8080**

### Step 3: Frontend Setup (2 minutes)

```bash
# Open new terminal, navigate to frontend
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

Frontend starts on: **http://localhost:5173**

### Step 4: Access Application

Open browser: **http://localhost:5173**

### Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@tutorly.com | admin123 |
| **Student** | student1@example.com | password123 |
| **Tutor** | john.smith@tutorly.com | password123 |

---

## 🐳 Quick Start (Docker)

### Prerequisites
- Docker
- Docker Compose

### One Command Start

```bash
docker-compose up -d
```

Access:
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **Database**: localhost:5432

### Stop

```bash
docker-compose down
```

---

## 📁 Project Structure

```
tutorly/
├── frontend/          # React + Vite
├── tutorly/          # Spring Boot
├── docker-compose.yml
└── README.md
```

---

## 🔧 Common Commands

### Backend
```bash
cd tutorly

# Run with dev profile
mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Build
mvnw clean package

# Run tests
mvnw test
```

### Frontend
```bash
cd frontend

# Development
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

---

## 🐛 Troubleshooting

### Port Already in Use

**Backend (8080):**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
kill -9 <PID>
```

**Frontend (5173):**
```bash
# Windows
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :5173
kill -9 <PID>
```

### Database Connection Failed

1. Check PostgreSQL is running
2. Verify credentials in `application.properties`
3. Ensure database `tutorly` exists

### CORS Errors

1. Check backend is running on port 8080
2. Verify CORS origins in `SecurityConfig.java`
3. Clear browser cache

---

## 📚 Next Steps

- Read [ARCHITECTURE.md](./ARCHITECTURE.md) for system design
- Read [DEPLOYMENT.md](./DEPLOYMENT.md) for production deployment
- Explore the API at http://localhost:8080/

---

## 🎯 Features

- ✅ JWT Authentication
- ✅ Role-based Access Control
- ✅ Tutor Discovery & Filtering
- ✅ Booking System
- ✅ Review System
- ✅ Admin Dashboard
- ✅ Dark Mode
- ✅ Responsive Design

---

**Need Help?** Check [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed instructions.
