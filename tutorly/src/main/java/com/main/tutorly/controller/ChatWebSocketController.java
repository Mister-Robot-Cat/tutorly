package com.main.tutorly.controller;

import com.main.tutorly.dto.ChatMessageDTO;
import com.main.tutorly.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        if (principal == null) {
            log.error("Unauthenticated WS request to send message");
            return;
        }

        log.debug("Received chat message from user {}: {}", principal.getName(), chatMessageDTO);

        // Save the message to DB first
        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO);

        // We need to notify the specific recipient about the new message
        // Stomp configured user destination prefix is "/user"
        // Target: /user/{recipientId}/queue/messages
        
        // However, standard principal routing uses username. 
        // We will send it to a general room topic, or we can send it to specific user if we mapped user principal correctly.
        // For simplicity and to avoid exposing username, let's route to a room-specific topic
        // Frontend will subscribe to /topic/room/{roomId}
        
        messagingTemplate.convertAndSend("/topic/room/" + savedMessage.getRoomId(), savedMessage);
    }
}
