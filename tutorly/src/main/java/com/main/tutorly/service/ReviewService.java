package com.main.tutorly.service;

import com.main.tutorly.dto.ReviewRequest;
import com.main.tutorly.entity.Booking;
import com.main.tutorly.entity.Review;
import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.BookingRepository;
import com.main.tutorly.repository.ReviewRepository;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    @Transactional
    public Review createReview(Long studentId, ReviewRequest request) {
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new RuntimeException("Review already exists for this booking");
        }
        
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("You can only review your own bookings");
        }
        
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed bookings");
        }
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Review review = new Review();
        review.setBooking(booking);
        review.setStudent(student);
        review.setTutor(booking.getTutor());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        review = reviewRepository.save(review);
        
        updateTutorRating(booking.getTutor().getId());
        
        return review;
    }
    
    private void updateTutorRating(Long tutorId) {
        TutorProfile tutor = tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        List<Review> reviews = reviewRepository.findByTutorId(tutorId);
        
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            
            tutor.setRating(BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));
            tutor.setTotalReviews(reviews.size());
            tutorProfileRepository.save(tutor);
        }
    }
    
    public List<Review> getTutorReviews(Long tutorId) {
        return reviewRepository.findByTutorId(tutorId);
    }
}
