package com.main.tutorly.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
