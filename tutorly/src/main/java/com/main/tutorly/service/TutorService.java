package com.main.tutorly.service;

import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.repository.TutorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService {
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    public List<TutorProfile> getAllTutors(String subject, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating) {
        List<TutorProfile> tutors = tutorProfileRepository.findAllActiveTutors();
        
        if (subject != null && !subject.isEmpty()) {
            tutors = tutors.stream()
                    .filter(t -> t.getSubjects().stream()
                            .anyMatch(s -> s.getName().equalsIgnoreCase(subject)))
                    .collect(Collectors.toList());
        }
        
        if (minPrice != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }
        
        if (maxPrice != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getHourlyRate().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }
        
        if (minRating != null) {
            tutors = tutors.stream()
                    .filter(t -> t.getRating().compareTo(minRating) >= 0)
                    .collect(Collectors.toList());
        }
        
        return tutors;
    }
    
    public TutorProfile getTutorById(Long id) {
        return tutorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
    }
    
    public TutorProfile updateTutorProfile(Long userId, TutorProfile updates) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        if (updates.getDescription() != null) {
            tutorProfile.setDescription(updates.getDescription());
        }
        if (updates.getHourlyRate() != null) {
            tutorProfile.setHourlyRate(updates.getHourlyRate());
        }
        if (updates.getExperienceYears() != null) {
            tutorProfile.setExperienceYears(updates.getExperienceYears());
        }
        if (updates.getEducation() != null) {
            tutorProfile.setEducation(updates.getEducation());
        }
        if (updates.getLanguages() != null) {
            tutorProfile.setLanguages(updates.getLanguages());
        }
        
        return tutorProfileRepository.save(tutorProfile);
    }
}
