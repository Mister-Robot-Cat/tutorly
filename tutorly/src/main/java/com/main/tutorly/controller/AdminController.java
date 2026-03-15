package com.main.tutorly.controller;

import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTutors", tutorProfileRepository.count());
        stats.put("activeTutors", tutorProfileRepository.findAllActiveTutors().size());
        return ResponseEntity.ok(stats);
    }
    
    @PatchMapping("/tutors/{id}/verify")
    public ResponseEntity<TutorProfile> verifyTutor(@PathVariable Long id) {
        TutorProfile tutor = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
        tutor.setIsVerified(true);
        return ResponseEntity.ok(tutorProfileRepository.save(tutor));
    }
    
    @PatchMapping("/tutors/{id}/active")
    public ResponseEntity<User> setTutorActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        TutorProfile tutor = tutorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
        User user = tutor.getUser();
        user.setIsActive(request.get("isActive"));
        return ResponseEntity.ok(userRepository.save(user));
    }
}
