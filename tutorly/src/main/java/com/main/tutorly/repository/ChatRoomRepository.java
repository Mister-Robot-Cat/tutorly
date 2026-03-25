package com.main.tutorly.repository;

import com.main.tutorly.entity.ChatRoom;
import com.main.tutorly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByStudentAndTutor(User student, User tutor);
    
    @Query("SELECT r FROM ChatRoom r WHERE r.student.id = :userId OR r.tutor.id = :userId")
    List<ChatRoom> findAllByUserId(Long userId);
}
