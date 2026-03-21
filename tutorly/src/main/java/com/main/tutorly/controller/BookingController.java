package com.main.tutorly.controller;

import com.main.tutorly.dto.BookingRequest;
import com.main.tutorly.entity.Booking;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private AuthService authService;
    
    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Creating booking for user: {}", email);
        
        Long studentId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(bookingService.createBooking(studentId, request));
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching bookings for user: {}", email);
        
        Long userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(bookingService.getStudentBookings(userId));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String statusStr = request.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status is required");
        }
        
        log.info("Updating booking ID: {} to status: {}", id, statusStr);
        Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusStr);
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
    
    @PatchMapping("/{id}/payment")
    public ResponseEntity<Booking> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
            
        String paymentStatusStr = request.get("paymentStatus");
        if (paymentStatusStr == null) {
            throw new IllegalArgumentException("Payment status is required");
        }
        
        log.info("Updating booking ID: {} payment status to: {}", id, paymentStatusStr);
        Booking.PaymentStatus paymentStatus = Booking.PaymentStatus.valueOf(paymentStatusStr);
        return ResponseEntity.ok(bookingService.updatePaymentStatus(id, paymentStatus));
    }
}
