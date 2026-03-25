package com.main.tutorly.service;

import com.main.tutorly.dto.ChatMessageDTO;
import com.main.tutorly.dto.ChatRoomDTO;
import com.main.tutorly.entity.ChatMessage;
import com.main.tutorly.entity.ChatRoom;
import com.main.tutorly.entity.User;
import com.main.tutorly.exception.ResourceNotFoundException;
import com.main.tutorly.repository.ChatMessageRepository;
import com.main.tutorly.repository.ChatRoomRepository;
import com.main.tutorly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomDTO getOrCreateRoom(Long userId, Long recipientId) {
        User user1 = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        User user2 = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found: " + recipientId));

        // Enforce consistent ordering: student first, tutor second to prevent duplicate rooms
        User student = user1.getRole() == User.Role.STUDENT ? user1 : user2;
        User tutor = user1.getRole() == User.Role.TUTOR ? user1 : user2;

        ChatRoom room = chatRoomRepository.findByStudentAndTutor(student, tutor)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom(student, tutor);
                    return chatRoomRepository.save(newRoom);
                });

        return mapToRoomDTO(room, userId);
    }

    public List<ChatRoomDTO> getUserRooms(Long userId) {
        return chatRoomRepository.findAllByUserId(userId).stream()
                .map(room -> mapToRoomDTO(room, userId))
                .collect(Collectors.toList());
    }

    public Page<ChatMessageDTO> getRoomMessages(Long roomId, Long userId, Pageable pageable) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        if (!room.getStudent().getId().equals(userId) && !room.getTutor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to chat room");
        }

        return chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(room, pageable)
                .map(this::mapToMessageDTO);
    }

    @Transactional
    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO) {
        ChatRoom room = chatRoomRepository.findById(messageDTO.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(messageDTO.getContent());
        message.setStatus(ChatMessage.MessageStatus.SENT);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return mapToMessageDTO(savedMessage);
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long currentUserId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomAndStatusAndSenderIdNot(
                room, ChatMessage.MessageStatus.SENT, currentUserId);

        unreadMessages.forEach(msg -> msg.setStatus(ChatMessage.MessageStatus.READ));
        chatMessageRepository.saveAll(unreadMessages);
    }

    private ChatRoomDTO mapToRoomDTO(ChatRoom room, Long currentUserId) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());

        User otherUser = room.getStudent().getId().equals(currentUserId) ? room.getTutor() : room.getStudent();
        dto.setOtherUserId(otherUser.getId());
        dto.setOtherUserName(otherUser.getFirstName() + " " + otherUser.getLastName());
        dto.setOtherUserRole(otherUser.getRole().name());

        // We fetch the latest message to show in the room list (can be optimized with a specific query)
        Page<ChatMessage> latestMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(
                room, org.springframework.data.domain.PageRequest.of(0, 1));
        
        if (latestMessages.hasContent()) {
            ChatMessage latest = latestMessages.getContent().get(0);
            dto.setLastMessage(latest.getContent());
            dto.setLastMessageTime(latest.getCreatedAt());
        }

        List<ChatMessage> unread = chatMessageRepository.findByChatRoomAndStatusAndSenderIdNot(
                room, ChatMessage.MessageStatus.SENT, currentUserId);
        dto.setUnreadCount(unread.size());

        return dto;
    }

    private ChatMessageDTO mapToMessageDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setRoomId(message.getChatRoom().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFirstName());
        dto.setContent(message.getContent());
        dto.setStatus(message.getStatus().name());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
