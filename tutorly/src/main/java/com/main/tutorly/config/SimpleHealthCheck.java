package com.main.tutorly.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SimpleHealthCheck {

    private final DataSource dataSource;

    public SimpleHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/actuator/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Connection connection = dataSource.getConnection();
            if (connection != null && connection.isValid(1)) {
                connection.close();
                response.put("status", "UP");
                response.put("database", "Available");
                response.put("timestamp", LocalDateTime.now());
            } else {
                response.put("status", "DOWN");
                response.put("database", "Unavailable");
            }
        } catch (SQLException e) {
            response.put("status", "DOWN");
            response.put("database", "Unavailable");
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/actuator/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("app", "Tutorly");
        response.put("version", "1.0.0");
        response.put("description", "Tutoring Marketplace Platform");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
