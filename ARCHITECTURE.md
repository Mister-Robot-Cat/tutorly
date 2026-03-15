# Tutorly - Production Architecture Documentation

## 🏗️ Architecture Overview

Tutorly follows a **separated frontend-backend architecture** for optimal scalability, maintainability, and deployment flexibility.

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                               │
│                    (Web Browser)                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTPS
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────┐          ┌──────────────┐
│   Frontend   │          │   Backend    │
│   (React)    │◄────────►│ (Spring Boot)│
│   Port 5173  │   REST   │   Port 8080  │
│              │   API    │              │
└──────────────┘          └──────┬───────┘
                                 │
                                 │ JDBC
                                 ▼
                          ┌──────────────┐
                          │  PostgreSQL  │
                          │   Database   │
                          │   Port 5432  │
                          └──────────────┘
```

## 📁 Project Structure

```
tutorly/
├── frontend/                      # React Frontend (Vite)
│   ├── src/
│   │   ├── components/           # Reusable UI components
│   │   │   ├── Navbar.jsx
│   │   │   └── ProtectedRoute.jsx
│   │   ├── context/              # React Context (Auth, Theme)
│   │   │   ├── AuthContext.jsx
│   │   │   └── ThemeContext.jsx
│   │   ├── pages/                # Page components
│   │   │   ├── Home.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── Tutors.jsx
│   │   │   ├── TutorDetail.jsx
│   │   │   ├── StudentDashboard.jsx
│   │   │   └── AdminDashboard.jsx
│   │   ├── services/             # API integration
│   │   │   └── api.js
│   │   ├── App.jsx               # Main app component
│   │   ├── main.jsx              # Entry point
│   │   └── index.css             # Global styles
│   ├── public/                   # Static assets
│   ├── .env.development          # Dev environment variables
│   ├── .env.production           # Prod environment variables
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js            # Vite configuration
│   ├── tailwind.config.js        # TailwindCSS config
│   ├── Dockerfile                # Production build
│   ├── Dockerfile.dev            # Development build
│   └── nginx.conf                # Nginx config for production
│
├── tutorly/                      # Spring Boot Backend
│   ├── src/main/java/com/main/tutorly/
│   │   ├── config/               # Configuration classes
│   │   │   ├── SecurityConfig.java
│   │   │   └── DataInitializer.java
│   │   ├── controller/           # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── TutorController.java
│   │   │   ├── BookingController.java
│   │   │   ├── ReviewController.java
│   │   │   ├── SubjectController.java
│   │   │   ├── AdminController.java
│   │   │   └── HomeController.java
│   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── BookingRequest.java
│   │   │   └── ReviewRequest.java
│   │   ├── entity/               # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── TutorProfile.java
│   │   │   ├── Subject.java
│   │   │   ├── Booking.java
│   │   │   └── Review.java
│   │   ├── repository/           # Data Access Layer
│   │   │   ├── UserRepository.java
│   │   │   ├── TutorProfileRepository.java
│   │   │   ├── SubjectRepository.java
│   │   │   ├── BookingRepository.java
│   │   │   └── ReviewRepository.java
│   │   ├── security/             # Security & JWT
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtRequestFilter.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── service/              # Business Logic
│   │   │   ├── AuthService.java
│   │   │   ├── TutorService.java
│   │   │   ├── BookingService.java
│   │   │   ├── ReviewService.java
│   │   │   └── SubjectService.java
│   │   └── TutorlyApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties         # Base config
│   │   ├── application-dev.properties     # Development
│   │   └── application-prod.properties    # Production
│   ├── pom.xml                   # Maven dependencies
│   └── Dockerfile                # Production build
│
├── docker-compose.yml            # Docker orchestration
├── .dockerignore
├── ARCHITECTURE.md               # This file
└── README.md                     # Project documentation
```

## 🔧 Technology Stack

### Frontend
- **React 18** - UI library
- **Vite** - Build tool and dev server (fast HMR)
- **React Router v6** - Client-side routing
- **TailwindCSS** - Utility-first CSS framework
- **Axios** - HTTP client for API calls
- **Lucide React** - Modern icon library
- **date-fns** - Date formatting and manipulation

### Backend
- **Spring Boot 4.0.3** - Application framework
- **Java 17** - Programming language
- **Spring Data JPA** - ORM with Hibernate
- **Spring Security** - Authentication & Authorization
- **JWT (jjwt)** - Token-based authentication
- **PostgreSQL** - Relational database
- **Lombok** - Reduce boilerplate code
- **BCrypt** - Password hashing

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Web server for frontend (production)
- **Maven** - Java build tool
- **npm** - JavaScript package manager

## 🔐 Security Architecture

### Authentication Flow
```
1. User submits credentials → POST /api/auth/login
2. Backend validates credentials
3. Backend generates JWT token
4. Frontend stores token in localStorage
5. Frontend includes token in Authorization header for protected requests
6. Backend validates token on each request
```

### CORS Configuration
- **Development**: `localhost:5173`, `localhost:3000`
- **Production**: Configured via environment variable `CORS_ALLOWED_ORIGINS`

### Security Headers
- X-Frame-Options: SAMEORIGIN
- X-Content-Type-Options: nosniff
- X-XSS-Protection: 1; mode=block
- Referrer-Policy: no-referrer-when-downgrade

## 🌐 API Communication

### Development
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Vite proxy: `/api` → `http://localhost:8080/api`

### Production
- Frontend: Served via Nginx on port 80
- Backend: Deployed separately (e.g., Heroku, AWS, DigitalOcean)
- API URL: Configured via `VITE_API_URL` environment variable

## 📊 Database Schema

### Core Entities
1. **User** - Base user information (students, tutors, admins)
2. **TutorProfile** - Extended tutor information
3. **Subject** - Course subjects (Math, Programming, etc.)
4. **Booking** - Lesson bookings
5. **Review** - Student reviews for tutors

### Relationships
- User (1) → (0..1) TutorProfile
- TutorProfile (N) ↔ (M) Subject
- User (1) → (N) Booking (as student)
- TutorProfile (1) → (N) Booking (as tutor)
- Booking (1) → (0..1) Review

## 🚀 Deployment Strategy

### Option 1: Separate Deployment (Recommended)

**Frontend:**
- Deploy to Vercel, Netlify, or AWS S3 + CloudFront
- Set `VITE_API_URL` to production backend URL
- Automatic builds on git push

**Backend:**
- Deploy to Heroku, AWS Elastic Beanstalk, or DigitalOcean
- Set environment variables for database and JWT
- Use managed PostgreSQL (AWS RDS, Heroku Postgres)

### Option 2: Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# Scale services
docker-compose up -d --scale backend=3
```

### Option 3: Kubernetes (Enterprise)
- Use Kubernetes manifests for orchestration
- Separate deployments for frontend, backend, database
- Horizontal pod autoscaling
- Ingress for routing

## 🔄 CI/CD Pipeline

### Recommended Tools
- **GitHub Actions** / GitLab CI / Jenkins
- **SonarQube** - Code quality analysis
- **Dependabot** - Dependency updates

### Pipeline Stages
1. **Build** - Compile and build artifacts
2. **Test** - Run unit and integration tests
3. **Security Scan** - SAST, dependency scanning
4. **Deploy to Staging** - Automated deployment
5. **Integration Tests** - E2E testing
6. **Deploy to Production** - Manual approval

## 📈 Monitoring & Observability

### Backend
- **Spring Boot Actuator** - Health checks, metrics
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization
- **ELK Stack** - Centralized logging

### Frontend
- **Sentry** - Error tracking
- **Google Analytics** - User analytics
- **Lighthouse** - Performance monitoring

## 🔒 Environment Variables

### Backend (Production)
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:5432/tutorly
DB_USERNAME=postgres
DB_PASSWORD=secure_password
JWT_SECRET=your_256_bit_secret
CORS_ALLOWED_ORIGINS=https://tutorly.com
```

### Frontend (Production)
```
VITE_API_URL=https://api.tutorly.com/api
VITE_APP_ENV=production
```

## 🧪 Testing Strategy

### Backend
- **Unit Tests** - JUnit 5, Mockito
- **Integration Tests** - Spring Boot Test, TestContainers
- **API Tests** - RestAssured

### Frontend
- **Unit Tests** - Vitest, React Testing Library
- **E2E Tests** - Playwright, Cypress
- **Visual Regression** - Percy, Chromatic

## 📝 Best Practices Implemented

✅ **Separation of Concerns** - Frontend and backend are independent  
✅ **Environment-based Configuration** - Dev, staging, prod configs  
✅ **Security First** - JWT, CORS, HTTPS, security headers  
✅ **Containerization** - Docker for consistent environments  
✅ **Health Checks** - Monitoring and auto-recovery  
✅ **Scalability** - Stateless backend, CDN for frontend  
✅ **Code Quality** - Linting, formatting, code reviews  
✅ **Documentation** - Comprehensive docs for developers  
✅ **Version Control** - Git with feature branches  
✅ **Automated Builds** - CI/CD pipelines  

## 🎯 Performance Optimizations

### Frontend
- Code splitting with Vite
- Lazy loading for routes
- Image optimization
- Gzip compression
- Browser caching
- CDN delivery

### Backend
- Database indexing
- Query optimization
- Connection pooling
- Caching (Redis for sessions)
- Async processing
- Load balancing

## 🔮 Future Enhancements

- [ ] WebSocket support for real-time notifications
- [ ] Redis caching layer
- [ ] Elasticsearch for advanced search
- [ ] Payment gateway integration (Stripe)
- [ ] Video conferencing integration (Zoom API)
- [ ] Mobile app (React Native)
- [ ] GraphQL API option
- [ ] Multi-language support (i18n)

---

**Last Updated:** March 2026  
**Architecture Version:** 1.0  
**Maintained by:** Development Team
