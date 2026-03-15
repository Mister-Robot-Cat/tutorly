package com.main.tutorly.repository;

import com.main.tutorly.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findByTutorId(Long tutorId);
    
    @Query("SELECT b FROM Booking b WHERE b.student.id = :studentId ORDER BY b.scheduledTime DESC")
    List<Booking> findStudentBookingsOrderByDate(@Param("studentId") Long studentId);
    
    @Query("SELECT b FROM Booking b WHERE b.tutor.id = :tutorId ORDER BY b.scheduledTime DESC")
    List<Booking> findTutorBookingsOrderByDate(@Param("tutorId") Long tutorId);
}
