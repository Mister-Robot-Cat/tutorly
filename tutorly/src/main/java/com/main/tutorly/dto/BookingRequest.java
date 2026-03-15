package com.main.tutorly.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull(message = "Tutor ID is required")
    private Long tutorId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;
    
    @NotNull(message = "Duration is required")
    private Integer durationMinutes;
    
    private String notes;
}
