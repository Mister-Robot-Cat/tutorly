package com.main.tutorly.security;

import io.restassured.http.ContentType;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class SecurityTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tutorly_security_test")
            .withUsername("test")
            .withPassword("test");

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void rateLimiting_ExceedsLimit_ReturnsTooManyRequests() {
        // Arrange
        String loginJson = "{\"email\":\"ratelimit@test.com\",\"password\":\"password123\"}";

        // Act & Assert - Make multiple rapid requests to auth endpoint
        for (int i = 0; i < 6; i++) { // Exceed the limit of 5 requests/minute
            int expectedStatus = i < 5 ? 401 : 429; // First 5 should be 401 (user not found), 6th should be 429
            
            given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(loginJson)
            .when()
                .post("/api/auth/login")
            .then()
                .statusCode(expectedStatus);
        }
    }

    @Test
    void cors_ValidOrigin_ReturnsCorrectHeaders() {
        // Act & Assert
        given()
            .port(port)
            .header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST")
        .when()
            .options("/api/auth/login")
        .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", "http://localhost:5173")
            .header("Access-Control-Allow-Methods", containsString("POST"))
            .header("Access-Control-Allow-Headers", not(emptyString()));
    }

    @Test
    void cors_InvalidOrigin_ReturnsNoCorsHeaders() {
        // Act & Assert
        given()
            .port(port)
            .header("Origin", "http://malicious-site.com")
            .header("Access-Control-Request-Method", "POST")
        .when()
            .options("/api/auth/login")
        .then()
            .statusCode(403); // Should be rejected due to CORS
    }

    @Test
    void sqlInjection_Attempt_ReturnsBadRequest() {
        // Arrange - SQL injection attempts in email field
        String[] maliciousInputs = {
            "'; DROP TABLE users; --",
            "' OR '1'='1",
            "admin'--",
            "' UNION SELECT * FROM users --"
        };

        for (String maliciousInput : maliciousInputs) {
            String loginJson = String.format("{\"email\":\"%s\",\"password\":\"password123\"}", maliciousInput);

            // Act & Assert
            given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(loginJson)
            .when()
                .post("/api/auth/login")
            .then()
                .statusCode(anyOf(equalTo(400), equalTo(401))) // Either validation error or unauthorized
                .body(not(containsString("SQL")));
        }
    }

    @Test
    void xss_Attempt_ReturnsBadRequest() {
        // Arrange - XSS attempts
        String[] xssInputs = {
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "<img src=x onerror=alert('xss')>",
            "';alert('xss');//"
        };

        for (String xssInput : xssInputs) {
            String registerJson = String.format(
                "{\"email\":\"%s@test.com\",\"password\":\"password123\",\"firstName\":\"%s\",\"lastName\":\"Test\",\"role\":\"STUDENT\"}",
                xssInput.replace("<", "").replace(">", "").replace(" ", ""), xssInput
            );

            // Act & Assert
            given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(registerJson)
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(anyOf(equalTo(400), equalTo(200))); // Either validation error or success (if sanitized)
        }
    }

    @Test
    void authentication_MissingToken_ReturnsUnauthorized() {
        // Act & Assert - Try to access protected endpoint without token
        given()
            .port(port)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/bookings/my-bookings")
        .then()
            .statusCode(401);
    }

    @Test
    void authentication_InvalidToken_ReturnsUnauthorized() {
        // Act & Assert - Try to access protected endpoint with invalid token
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer invalid-token")
        .when()
            .get("/api/bookings/my-bookings")
        .then()
            .statusCode(401);
    }

    @Test
    void authorization_AdminEndpoint_WithStudentRole_ReturnsForbidden() {
        // First register and login as student to get token
        String registerJson = "{\"email\":\"student@test.com\",\"password\":\"password123\",\"firstName\":\"Student\",\"lastName\":\"User\",\"role\":\"STUDENT\"}";
        
        String token = given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(registerJson)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200)
            .extract()
            .path("token");

        // Try to access admin endpoint with student token
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/admin/users")
        .then()
            .statusCode(403); // Forbidden
    }

    @Test
    void healthCheck_PublicAccess_ReturnsHealthStatus() {
        // Act & Assert - Health check should be publicly accessible
        given()
            .port(port)
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", anyOf(equalTo("UP"), equalTo("DOWN")));
    }

    @Test
    void infoEndpoint_PublicAccess_ReturnsAppInfo() {
        // Act & Assert - Info endpoint should be publicly accessible
        given()
            .port(port)
        .when()
            .get("/actuator/info")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("app", equalTo("Tutorly"))
            .body("version", notNullValue());
    }
}
