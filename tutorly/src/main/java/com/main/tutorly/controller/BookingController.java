package com.main.tutorly.controller;

import com.main.tutorly.dto.BookingRequest;
import com.main.tutorly.entity.Booking;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        Long studentId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(bookingService.createBooking(studentId, request));
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        Long userId = authService.getCurrentUser(email).getId();
        return ResponseEntity.ok(bookingService.getStudentBookings(userId));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Booking.BookingStatus status = Booking.BookingStatus.valueOf(request.get("status"));
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
    
    @PatchMapping("/{id}/payment")
    public ResponseEntity<Booking> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Booking.PaymentStatus paymentStatus = Booking.PaymentStatus.valueOf(request.get("paymentStatus"));
        return ResponseEntity.ok(bookingService.updatePaymentStatus(id, paymentStatus));
    }
}
