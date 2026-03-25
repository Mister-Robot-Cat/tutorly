package com.main.tutorly.controller;

import com.main.tutorly.dto.ChatMessageDTO;
import com.main.tutorly.dto.ChatRoomDTO;
import com.main.tutorly.entity.User;
import com.main.tutorly.service.AuthService;
import com.main.tutorly.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {

    private final ChatService chatService;
    private final AuthService authService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDTO> getOrCreateRoom(@RequestBody Map<String, Long> payload, Authentication auth) {
        Long recipientId = payload.get("recipientId");
        User currentUser = authService.getCurrentUser(auth.getName());
        log.info("User {} requesting chat room with User {}", currentUser.getId(), recipientId);
        return ResponseEntity.ok(chatService.getOrCreateRoom(currentUser.getId(), recipientId));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getUserRooms(Authentication auth) {
        User currentUser = authService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(chatService.getUserRooms(currentUser.getId()));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication auth) {
        User currentUser = authService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, currentUser.getId(), PageRequest.of(page, size)));
    }

    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long roomId, Authentication auth) {
        User currentUser = authService.getCurrentUser(auth.getName());
        chatService.markMessagesAsRead(roomId, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
