package com.main.tutorly.controller;

import com.main.tutorly.dto.ReviewRequest;
import com.main.tutorly.entity.Review;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private AuthService authService;
    
    @PostMapping
    public ResponseEntity<Review> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        Long studentId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(reviewService.createReview(studentId, request));
    }
    
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<Review>> getTutorReviews(@PathVariable Long tutorId) {
        return ResponseEntity.ok(reviewService.getTutorReviews(tutorId));
    }
}
