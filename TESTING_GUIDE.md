# 🧪 Testing Guide - Tutorly Platform

## 📋 Table of Contents

1. [Overview](#overview)
2. [Backend Testing](#backend-testing)
3. [Frontend Testing](#frontend-testing)
4. [Integration Testing](#integration-testing)
5. [Security Testing](#security-testing)
6. [E2E Testing](#e2e-testing)
7. [Running Tests](#running-tests)
8. [Test Coverage](#test-coverage)
9. [Best Practices](#best-practices)

---

## 🎯 Overview

Tutorly has a comprehensive testing strategy covering multiple levels of the application pyramid:

```
    E2E Tests (Playwright)
         ↑
    Integration Tests (TestContainers)
         ↑
    Unit Tests (JUnit + Vitest)
```

### Test Coverage Goals
- **Unit Tests**: 80%+ coverage for business logic
- **Integration Tests**: All API endpoints
- **E2E Tests**: Critical user journeys
- **Security Tests**: Authentication, authorization, rate limiting

---

## 📦 Backend Testing

### Unit Tests

**Location**: `tutorly/src/test/java/com/main/tutorly/`

**Technologies**:
- JUnit 5
- Mockito
- Spring Boot Test

**Examples**:

#### Service Layer Tests
```bash
# Run all service tests
./mvnw test -Dtest=**/service/**Test

# Run specific service test
./mvnw test -Dtest=AuthServiceTest
```

#### Controller Layer Tests
```bash
# Run all controller tests
./mvnw test -Dtest=**/controller/**Test

# Run specific controller test
./mvnw test -Dtest=AuthControllerTest
```

### Integration Tests

**Location**: `tutorly/src/test/java/com/main/tutorly/integration/`

**Technologies**:
- TestContainers
- PostgreSQL
- RestAssured

**Examples**:
```bash
# Run all integration tests
./mvnw test -Dtest=**/integration/**Test

# Run specific integration test
./mvnw test -Dtest=AuthIntegrationTest
```

### Security Tests

**Location**: `tutorly/src/test/java/com/main/tutorly/security/`

**Coverage**:
- Rate limiting
- CORS configuration
- Authentication/Authorization
- Input validation (SQL injection, XSS)

```bash
# Run security tests
./mvnw test -Dtest=SecurityTest
```

---

## 🎨 Frontend Testing

### Unit Tests

**Location**: `frontend/src/test/`

**Technologies**:
- Vitest
- React Testing Library
- jsdom

**Examples**:

#### Component Tests
```bash
cd frontend

# Run all unit tests
npm test

# Run specific component test
npm test -- Navbar.test.jsx

# Run tests in watch mode
npm test -- --watch

# Run tests with UI
npm run test:ui
```

#### Test Coverage
```bash
# Generate coverage report
npm run test:coverage

# View coverage report
open coverage/index.html
```

### E2E Tests

**Location**: `frontend/e2e/`

**Technologies**:
- Playwright

**Browsers Supported**:
- Chromium
- Firefox
- WebKit (Safari)
- Mobile Chrome

**Examples**:
```bash
cd frontend

# Run all E2E tests
npm run test:e2e

# Run specific E2E test
npm run test:e2e -- auth.spec.js

# Run E2E tests with UI
npm run test:e2e:ui

# Run E2E tests on specific browser
npm run test:e2e -- --project=chromium
```

---

## 🔗 Integration Testing

### Database Integration

**TestContainers Configuration**:
```java
@Testcontainers
class AuthIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tutorly_test")
            .withUsername("test")
            .withPassword("test");
}
```

### API Integration

**RestAssured Examples**:
```java
@Test
void registerAndLogin_CompleteFlow_Success() {
    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(registerRequest)
    .when()
        .post("/api/auth/register")
    .then()
        .statusCode(200)
        .body("token", not(emptyString()));
}
```

---

## 🔒 Security Testing

### Rate Limiting Tests

```java
@Test
void rateLimiting_ExceedsLimit_ReturnsTooManyRequests() {
    // Make multiple rapid requests
    for (int i = 0; i < 6; i++) {
        int expectedStatus = i < 5 ? 401 : 429;
        // ... test implementation
    }
}
```

### Authentication Tests

```java
@Test
void authentication_MissingToken_ReturnsUnauthorized() {
    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get("/api/bookings/my-bookings")
    .then()
        .statusCode(401);
}
```

### Input Validation Tests

```java
@Test
void sqlInjection_Attempt_ReturnsBadRequest() {
    String[] maliciousInputs = {
        "'; DROP TABLE users; --",
        "' OR '1'='1",
        "admin'--"
    };
    
    for (String maliciousInput : maliciousInputs) {
        // Test SQL injection attempts
    }
}
```

---

## 🎭 E2E Testing

### Critical User Journeys

#### 1. Authentication Flow
```javascript
test('user can register and login successfully', async ({ page }) => {
    await page.goto('/')
    await page.click('text=Register')
    await page.fill('input[name="email"]', 'e2e@test.com')
    await page.fill('input[name="password"]', 'password123')
    await page.click('button[type="submit"]')
    await expect(page).toHaveURL('/dashboard')
})
```

#### 2. Navigation Flow
```javascript
test('navigation links work correctly', async ({ page }) => {
    await page.goto('/')
    await page.click('text=Find Tutors')
    await expect(page).toHaveURL('/tutors')
    await expect(page.locator('text=Available Tutors')).toBeVisible()
})
```

#### 3. Mobile Responsiveness
```javascript
test.describe('Mobile Navigation', () => {
    test.use({ viewport: { width: 375, height: 667 } })
    
    test('mobile menu works', async ({ page }) => {
        await page.goto('/')
        await page.click('button[aria-label="Toggle menu"]')
        await expect(page.locator('text=Login')).toBeVisible()
    })
})
```

---

## 🚀 Running Tests

### Quick Test Commands

```bash
# Backend Tests
cd tutorly
./mvnw test                                    # All tests
./mvnw test -Dtest=AuthServiceTest            # Specific test
./mvnw test -Dtest=**/service/**Test          # Service tests
./mvnw test -Dtest=**/integration/**Test      # Integration tests

# Frontend Tests
cd frontend
npm test                                      # Unit tests
npm run test:e2e                              # E2E tests
npm run test:coverage                         # Coverage report

# All Tests (from project root)
./test.sh                                      # Comprehensive test suite
```

### Test Profiles

**Development**: Uses H2 in-memory database for fast tests
**Test**: Uses TestContainers with PostgreSQL for realistic testing
**Production**: No tests, optimized for performance

---

## 📊 Test Coverage

### Backend Coverage

**Current Coverage**: ~75% (target: 80%+)

**Coverage Areas**:
- ✅ Service layer: 85%
- ✅ Controller layer: 70%
- ✅ Security components: 90%
- ⚠️ Repository layer: 60%

**View Coverage Report**:
```bash
cd tutorly
./mvnw jacoco:report
open target/site/jacoco/index.html
```

### Frontend Coverage

**Current Coverage**: ~65% (target: 80%+)

**Coverage Areas**:
- ✅ Components: 70%
- ✅ Services: 60%
- ⚠️ Utils: 50%
- ⚠️ Hooks: 40%

**View Coverage Report**:
```bash
cd frontend
npm run test:coverage
open coverage/index.html
```

---

## 📝 Best Practices

### Test Organization

1. **Arrange-Act-Assert Pattern**
   ```java
   @Test
   void someTest() {
       // Arrange
       when(mockService.getData()).thenReturn(data);
       
       // Act
       Result result = service.process();
       
       // Assert
       assertThat(result).isNotNull();
   }
   ```

2. **Descriptive Test Names**
   ```java
   // Good
   void register_ValidRequest_ReturnsSuccessResponse()
   
   // Bad
   void test1()
   ```

3. **Test Independence**
   - Each test should run independently
   - Use `@BeforeEach` for setup
   - Clean up after tests

### Mocking Guidelines

1. **Mock External Dependencies**
   ```java
   @Mock
   private UserRepository userRepository;
   
   @Mock
   private EmailService emailService;
   ```

2. **Don't Mock Everything**
   - Mock only external dependencies
   - Test real business logic
   - Use TestContainers for database tests

### Test Data Management

1. **Use Builders for Test Data**
   ```java
   User testUser = User.builder()
       .email("test@example.com")
       .firstName("Test")
       .lastName("User")
       .build();
   ```

2. **Reuse Test Fixtures**
   ```java
   @BeforeEach
   void setUp() {
       testUser = createTestUser();
   }
   ```

### CI/CD Integration

1. **Fast Feedback**
   - Run unit tests first
   - Run integration tests in parallel
   - Run E2E tests last

2. **Fail Fast**
   ```yaml
   # GitHub Actions example
   - name: Run Tests
     run: |
       ./mvnw test
       npm test
   ```

---

## 🔧 Troubleshooting

### Common Issues

1. **TestContainers Not Starting**
   ```bash
   # Enable Docker daemon
   # Check Docker permissions
   docker ps
   ```

2. **Port Conflicts**
   ```bash
   # Use random ports in tests
   @LocalServerPort
   private int port;
   ```

3. **Mockito Issues**
   ```bash
   # Check mock annotations
   @ExtendWith(MockitoExtension.class)
   @Mock private SomeService someService;
   ```

4. **Frontend Test Issues**
   ```bash
   # Clear test cache
   rm -rf node_modules/.vite
   npm install
   ```

### Debugging Tests

1. **Enable Debug Logging**
   ```properties
   # application-test.properties
   logging.level.com.main.tutorly=DEBUG
   ```

2. **Run Tests with Debugger**
   ```bash
   # Maven debug
   ./mvnw test -Dmaven.surefire.debug
   
   # Frontend debug
   npm test -- --inspect-brk
   ```

---

## 📈 Next Steps

### Immediate Improvements

1. **Increase Test Coverage**
   - Add more unit tests for edge cases
   - Improve repository layer coverage
   - Add more component tests

2. **Performance Testing**
   - Load testing with JMeter
   - Stress testing for API endpoints
   - Database performance tests

3. **Accessibility Testing**
   - Axe automated tests
   - Screen reader testing
   - Keyboard navigation tests

### Long-term Goals

1. **Contract Testing**
   - Consumer-driven contracts
   - API compatibility testing
   - Version compatibility

2. **Chaos Engineering**
   - Failure injection testing
   - Network partition testing
   - Database failure testing

3. **Visual Regression Testing**
   - Percy or Chromatic integration
   - Cross-browser visual testing
   - Mobile visual testing

---

**Last Updated**: 2026-03-18  
**Test Framework Version**: 1.0  
**Next Review**: 2026-04-18
