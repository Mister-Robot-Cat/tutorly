package com.main.tutorly.repository;

import com.main.tutorly.entity.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
    Optional<TutorProfile> findByUserId(Long userId);
    
    @Query("SELECT t FROM TutorProfile t WHERE t.user.isActive = true")
    List<TutorProfile> findAllActiveTutors();
    
    @Query("SELECT t FROM TutorProfile t JOIN t.subjects s WHERE s.name = :subjectName AND t.user.isActive = true")
    List<TutorProfile> findBySubjectName(@Param("subjectName") String subjectName);
    
    @Query("SELECT t FROM TutorProfile t WHERE t.hourlyRate BETWEEN :minPrice AND :maxPrice AND t.user.isActive = true")
    List<TutorProfile> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT t FROM TutorProfile t WHERE t.rating >= :minRating AND t.user.isActive = true")
    List<TutorProfile> findByMinRating(@Param("minRating") BigDecimal minRating);
}
