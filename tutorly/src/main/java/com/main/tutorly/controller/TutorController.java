package com.main.tutorly.controller;

import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {
    
    @Autowired
    private TutorService tutorService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping
    public ResponseEntity<List<TutorProfile>> getAllTutors(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating) {
        return ResponseEntity.ok(tutorService.getAllTutors(subject, minPrice, maxPrice, minRating));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TutorProfile> getTutorById(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.getTutorById(id));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<TutorProfile> updateProfile(
            @RequestBody TutorProfile updates,
            Authentication authentication) {
        String email = authentication.getName();
        Long userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(tutorService.updateTutorProfile(userId, updates));
    }
}
