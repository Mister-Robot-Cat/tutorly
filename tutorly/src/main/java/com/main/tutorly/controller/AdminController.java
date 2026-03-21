package com.main.tutorly.controller;

import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.exception.ResourceNotFoundException;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users for admin dashboard");
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Gathering platform statistics");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTutors", tutorProfileRepository.count());
        stats.put("activeTutors", tutorProfileRepository.findAllActiveTutors().size());
        return ResponseEntity.ok(stats);
    }
    
    @PatchMapping("/tutors/{id}/verify")
    public ResponseEntity<TutorProfile> verifyTutor(@PathVariable Long id) {
        log.info("Verifying tutor with ID: {}", id);
        TutorProfile tutor = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor not found with ID: " + id));
        
        tutor.setIsVerified(true);
        return ResponseEntity.ok(tutorProfileRepository.save(tutor));
    }
    
    @PatchMapping("/tutors/{id}/active")
    public ResponseEntity<User> setTutorActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        
        Boolean isActive = request.get("isActive");
        log.info("Setting active status for tutor ID: {} to {}", id, isActive);
        
        TutorProfile tutor = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor not found with ID: " + id));
                
        User user = tutor.getUser();
        user.setIsActive(isActive);
        return ResponseEntity.ok(userRepository.save(user));
    }
}
