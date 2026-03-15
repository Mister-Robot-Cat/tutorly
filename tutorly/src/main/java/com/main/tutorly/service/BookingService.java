package com.main.tutorly.service;

import com.main.tutorly.dto.BookingRequest;
import com.main.tutorly.entity.Booking;
import com.main.tutorly.entity.Subject;
import com.main.tutorly.entity.TutorProfile;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.BookingRepository;
import com.main.tutorly.repository.SubjectRepository;
import com.main.tutorly.repository.TutorProfileRepository;
import com.main.tutorly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TutorProfileRepository tutorProfileRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Transactional
    public Booking createBooking(Long studentId, BookingRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        TutorProfile tutor = tutorProfileRepository.findById(request.getTutorId())
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        BigDecimal totalPrice = tutor.getHourlyRate()
                .multiply(BigDecimal.valueOf(request.getDurationMinutes()))
                .divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
        
        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setTutor(tutor);
        booking.setSubject(subject);
        booking.setScheduledTime(request.getScheduledTime());
        booking.setDurationMinutes(request.getDurationMinutes());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        booking.setNotes(request.getNotes());
        booking.setMeetingLink("https://meet.tutorly.com/" + UUID.randomUUID().toString());
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> getStudentBookings(Long studentId) {
        return bookingRepository.findStudentBookingsOrderByDate(studentId);
    }
    
    public List<Booking> getTutorBookings(Long tutorId) {
        return bookingRepository.findTutorBookingsOrderByDate(tutorId);
    }
    
    @Transactional
    public Booking updateBookingStatus(Long bookingId, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(status);
        
        if (status == Booking.BookingStatus.COMPLETED) {
            TutorProfile tutor = booking.getTutor();
            tutor.setTotalLessons(tutor.getTotalLessons() + 1);
            tutorProfileRepository.save(tutor);
        }
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking updatePaymentStatus(Long bookingId, Booking.PaymentStatus paymentStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setPaymentStatus(paymentStatus);
        
        if (paymentStatus == Booking.PaymentStatus.PAID) {
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
        }
        
        return bookingRepository.save(booking);
    }
}
