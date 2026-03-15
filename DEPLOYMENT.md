# Deployment Guide - Tutorly Platform

Complete guide for deploying Tutorly in different environments.

## 📋 Table of Contents

1. [Local Development](#local-development)
2. [Docker Deployment](#docker-deployment)
3. [Production Deployment](#production-deployment)
4. [Environment Variables](#environment-variables)
5. [Troubleshooting](#troubleshooting)

---

## 🖥️ Local Development

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.9+

### Step 1: Database Setup

```bash
# Create PostgreSQL database
psql -U postgres
CREATE DATABASE tutorly;
\q
```

### Step 2: Backend Setup

```bash
# Navigate to backend directory
cd tutorly

# Update application-dev.properties with your database credentials
# File: src/main/resources/application-dev.properties

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or on Windows
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend will start on `http://localhost:8080`

### Step 3: Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend will start on `http://localhost:5173`

### Step 4: Access the Application

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **API Health**: http://localhost:8080/

### Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@tutorly.com | admin123 |
| Student | student1@example.com | password123 |
| Tutor | john.smith@tutorly.com | password123 |

---

## 🐳 Docker Deployment

### Prerequisites
- Docker 24+
- Docker Compose 2.20+

### Quick Start

```bash
# Clone repository
git clone <repository-url>
cd tutorly

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Services

- **PostgreSQL**: `localhost:5432`
- **Backend**: `localhost:8080`
- **Frontend**: `localhost:5173`

### Rebuild After Changes

```bash
# Rebuild specific service
docker-compose up -d --build backend

# Rebuild all services
docker-compose up -d --build
```

---

## 🚀 Production Deployment

### Option 1: Separate Deployment (Recommended)

#### Frontend Deployment (Vercel/Netlify)

**Vercel:**
```bash
# Install Vercel CLI
npm i -g vercel

# Navigate to frontend
cd frontend

# Deploy
vercel --prod
```

**Netlify:**
```bash
# Install Netlify CLI
npm i -g netlify-cli

# Navigate to frontend
cd frontend

# Build
npm run build

# Deploy
netlify deploy --prod --dir=dist
```

**Environment Variables (Vercel/Netlify):**
```
VITE_API_URL=https://api.yourdomain.com/api
VITE_APP_ENV=production
```

#### Backend Deployment (Heroku)

```bash
# Install Heroku CLI
# https://devcenter.heroku.com/articles/heroku-cli

# Login
heroku login

# Create app
heroku create tutorly-api

# Add PostgreSQL
heroku addons:create heroku-postgresql:essential-0

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your_production_secret_min_256_bits
heroku config:set CORS_ALLOWED_ORIGINS=https://tutorly.vercel.app

# Deploy
cd tutorly
git init
heroku git:remote -a tutorly-api
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

#### Backend Deployment (AWS Elastic Beanstalk)

```bash
# Install EB CLI
pip install awsebcli

# Initialize
cd tutorly
eb init -p java-17 tutorly-backend --region us-east-1

# Create environment
eb create tutorly-prod

# Set environment variables
eb setenv SPRING_PROFILES_ACTIVE=prod
eb setenv DATABASE_URL=jdbc:postgresql://your-rds-endpoint:5432/tutorly
eb setenv DB_USERNAME=postgres
eb setenv DB_PASSWORD=your_password
eb setenv JWT_SECRET=your_secret
eb setenv CORS_ALLOWED_ORIGINS=https://tutorly.com

# Deploy
eb deploy

# Open application
eb open
```

### Option 2: Docker Production Deployment

#### Build Production Images

```bash
# Build backend
cd tutorly
docker build -t tutorly-backend:latest .

# Build frontend
cd ../frontend
docker build -t tutorly-frontend:latest .
```

#### Push to Docker Registry

```bash
# Tag images
docker tag tutorly-backend:latest your-registry/tutorly-backend:latest
docker tag tutorly-frontend:latest your-registry/tutorly-frontend:latest

# Push to registry
docker push your-registry/tutorly-backend:latest
docker push your-registry/tutorly-frontend:latest
```

#### Deploy to Server

```bash
# SSH to server
ssh user@your-server.com

# Pull images
docker pull your-registry/tutorly-backend:latest
docker pull your-registry/tutorly-frontend:latest

# Run with docker-compose
docker-compose -f docker-compose.prod.yml up -d
```

---

## 🔐 Environment Variables

### Backend Environment Variables

#### Development (application-dev.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorly
spring.datasource.username=postgres
spring.datasource.password=postgres
jwt.secret=dev_secret_key
cors.allowed-origins=http://localhost:5173,http://localhost:3000
```

#### Production (Set via Environment)
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://production-host:5432/tutorly
DB_USERNAME=tutorly_user
DB_PASSWORD=secure_password_here
JWT_SECRET=production_jwt_secret_min_256_bits_long
CORS_ALLOWED_ORIGINS=https://tutorly.com,https://www.tutorly.com
PORT=8080
```

### Frontend Environment Variables

#### Development (.env.development)
```
VITE_API_URL=http://localhost:8080/api
VITE_APP_ENV=development
```

#### Production (.env.production)
```
VITE_API_URL=https://api.tutorly.com/api
VITE_APP_ENV=production
```

---

## 🔍 Health Checks

### Backend Health Check
```bash
curl http://localhost:8080/
```

Expected response:
```json
{
  "message": "Welcome to Tutorly API",
  "version": "1.0.0",
  "status": "running"
}
```

### Frontend Health Check
```bash
curl http://localhost:5173/
```

Should return the HTML of the React app.

---

## 🐛 Troubleshooting

### Backend Issues

**Problem: Port 8080 already in use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Mac/Linux

# Kill the process
taskkill /PID <PID> /F         # Windows
kill -9 <PID>                  # Mac/Linux
```

**Problem: Database connection failed**
```bash
# Check PostgreSQL is running
pg_isready -h localhost -p 5432

# Check credentials in application.properties
# Verify database exists
psql -U postgres -l
```

**Problem: JWT token invalid**
- Ensure JWT_SECRET is set correctly
- Check token expiration (default 24 hours)
- Clear localStorage in browser

### Frontend Issues

**Problem: API calls failing with CORS error**
- Check CORS configuration in SecurityConfig.java
- Verify allowed origins match frontend URL
- Check browser console for exact error

**Problem: Vite dev server not starting**
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Try different port
npm run dev -- --port 5174
```

**Problem: Build fails**
```bash
# Clear Vite cache
rm -rf node_modules/.vite

# Rebuild
npm run build
```

### Docker Issues

**Problem: Container won't start**
```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend

# Restart specific service
docker-compose restart backend
```

**Problem: Database data lost**
```bash
# Check volumes
docker volume ls

# Backup volume
docker run --rm -v tutorly_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/db-backup.tar.gz /data
```

---

## 📊 Monitoring

### Backend Monitoring

Add Spring Boot Actuator endpoints:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Access metrics:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

### Frontend Monitoring

Use browser DevTools:
- Network tab for API calls
- Console for errors
- Performance tab for load times

---

## 🔄 CI/CD Setup

### GitHub Actions Example

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy

on:
  push:
    branches: [ main ]

jobs:
  deploy-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build with Maven
        run: cd tutorly && mvn clean package
      - name: Deploy to Heroku
        uses: akhileshns/heroku-deploy@v3.12.14
        with:
          heroku_api_key: ${{secrets.HEROKU_API_KEY}}
          heroku_app_name: "tutorly-api"
          heroku_email: "your-email@example.com"

  deploy-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup Node
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install and Build
        run: cd frontend && npm ci && npm run build
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v20
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.ORG_ID}}
          vercel-project-id: ${{ secrets.PROJECT_ID}}
          vercel-args: '--prod'
```

---

## 📝 Checklist Before Production

- [ ] Change all default passwords
- [ ] Set strong JWT secret (min 256 bits)
- [ ] Configure HTTPS/SSL certificates
- [ ] Set up database backups
- [ ] Configure monitoring and alerts
- [ ] Enable rate limiting
- [ ] Review and test CORS settings
- [ ] Set up error tracking (Sentry)
- [ ] Configure CDN for frontend
- [ ] Enable database connection pooling
- [ ] Set up log aggregation
- [ ] Test disaster recovery plan
- [ ] Document runbooks for common issues

---

**Need Help?** Check the [ARCHITECTURE.md](./ARCHITECTURE.md) for detailed system design.
