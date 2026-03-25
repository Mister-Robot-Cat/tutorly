package com.main.tutorly.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomDTO {
    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserRole;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
}
