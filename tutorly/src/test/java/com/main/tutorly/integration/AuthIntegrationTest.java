package com.main.tutorly.integration;

import com.main.tutorly.dto.AuthResponse;
import com.main.tutorly.dto.LoginRequest;
import com.main.tutorly.dto.RegisterRequest;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.UserRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tutorly_test")
            .withUsername("test")
            .withPassword("test");

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerAndLogin_CompleteFlow_Success() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Integration");
        registerRequest.setLastName("Test");
        registerRequest.setRole(User.Role.STUDENT);

        // Act & Assert - Register
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("token", not(emptyString()))
            .body("userId", notNullValue())
            .body("email", equalTo("integration@test.com"))
            .body("role", equalTo("STUDENT"));

        // Verify user is saved in database
        User savedUser = userRepository.findByEmail("integration@test.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("integration@test.com", savedUser.getEmail());
        assertEquals("Integration", savedUser.getFirstName());

        // Act & Assert - Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("password123");

        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("token", not(emptyString()))
            .body("userId", equalTo(savedUser.getId().intValue()))
            .body("email", equalTo("integration@test.com"))
            .body("role", equalTo("STUDENT"));
    }

    @Test
    void register_DuplicateEmail_ReturnsBadRequest() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("duplicate@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("First");
        registerRequest.setLastName("User");
        registerRequest.setRole(User.Role.STUDENT);

        // Register first user
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200);

        // Try to register with same email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setEmail("duplicate@test.com");
        duplicateRequest.setPassword("password456");
        duplicateRequest.setFirstName("Second");
        duplicateRequest.setLastName("User");
        duplicateRequest.setRole(User.Role.TUTOR);

        // Act & Assert
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(duplicateRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body(containsString("Email already exists"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("invalid@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Invalid");
        registerRequest.setLastName("Test");
        registerRequest.setRole(User.Role.STUDENT);

        // Register user
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200);

        // Try to login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid@test.com");
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401)
            .body(containsString("Invalid credentials"));
    }

    @Test
    void login_NonExistentUser_ReturnsUnauthorized() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401)
            .body(containsString("User not found"));
    }

    @Test
    void register_InvalidData_ReturnsBadRequest() {
        // Arrange - Invalid email
        RegisterRequest invalidEmailRequest = new RegisterRequest();
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setPassword("password123");
        invalidEmailRequest.setFirstName("Test");
        invalidEmailRequest.setLastName("User");
        invalidEmailRequest.setRole(User.Role.STUDENT);

        // Act & Assert
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(invalidEmailRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);

        // Arrange - Short password
        RegisterRequest shortPasswordRequest = new RegisterRequest();
        shortPasswordRequest.setEmail("test@example.com");
        shortPasswordRequest.setPassword("123"); // Too short
        shortPasswordRequest.setFirstName("Test");
        shortPasswordRequest.setLastName("User");
        shortPasswordRequest.setRole(User.Role.STUDENT);

        // Act & Assert
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(shortPasswordRequest)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }
}
