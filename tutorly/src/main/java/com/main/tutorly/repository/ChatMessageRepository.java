package com.main.tutorly.repository;

import com.main.tutorly.entity.ChatMessage;
import com.main.tutorly.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);
    List<ChatMessage> findByChatRoomAndStatusAndSenderIdNot(ChatRoom chatRoom, ChatMessage.MessageStatus status, Long senderId);
}
