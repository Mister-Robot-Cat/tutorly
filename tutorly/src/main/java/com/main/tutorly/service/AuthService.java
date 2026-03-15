package com.main.tutorly.service;

import com.main.tutorly.dto.AuthResponse;
import com.main.tutorly.dto.LoginRequest;
import com.main.tutorly.dto.RegisterRequest;
import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import com.main.tutorly.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setIsActive(true);
        
        user = userRepository.save(user);
        
        if (request.getRole() == User.Role.TUTOR) {
            TutorProfile tutorProfile = new TutorProfile();
            tutorProfile.setUser(user);
            tutorProfile.setDescription(request.getDescription() != null ? request.getDescription() : "");
            tutorProfile.setHourlyRate(request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.valueOf(25));
            tutorProfile.setExperienceYears(0);
            tutorProfile.setIsVerified(false);
            tutorProfile.setTotalLessons(0);
            tutorProfile.setRating(BigDecimal.ZERO);
            tutorProfile.setTotalReviews(0);
            
            tutorProfileRepository.save(tutorProfile);
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        
        return new AuthResponse(token, user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName(), user.getRole());
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new AuthResponse(token, user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName(), user.getRole());
    }
    
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
