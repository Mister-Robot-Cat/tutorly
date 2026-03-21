package com.main.tutorly.controller;

import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.TutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        log.info("Fetching all tutors with filters");
        return ResponseEntity.ok(tutorService.getAllTutors(subject, minPrice, maxPrice, minRating));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<TutorProfile>> getAllTutorsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy) {
        log.info("Fetching tutors page {} size {} sorted by {}", page, size, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return ResponseEntity.ok(tutorService.getAllTutorsPaged(pageRequest));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TutorProfile> getTutorById(@PathVariable Long id) {
        log.info("Fetching tutor with ID: {}", id);
        return ResponseEntity.ok(tutorService.getTutorById(id));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<TutorProfile> updateProfile(
            @RequestBody TutorProfile updates,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Updating profile for tutor: {}", email);
        
        Long userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(tutorService.updateTutorProfile(userId, updates));
    }
}
