package com.main.tutorly.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Tutorly API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("auth", "/api/auth");
        endpoints.put("tutors", "/api/tutors");
        endpoints.put("bookings", "/api/bookings");
        endpoints.put("reviews", "/api/reviews");
        endpoints.put("subjects", "/api/subjects");
        endpoints.put("admin", "/api/admin");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> docs = new HashMap<>();
        docs.put("register", "POST /api/auth/register");
        docs.put("login", "POST /api/auth/login");
        docs.put("getTutors", "GET /api/tutors");
        docs.put("getSubjects", "GET /api/subjects");
        
        response.put("quickStart", docs);
        
        return response;
    }
}
