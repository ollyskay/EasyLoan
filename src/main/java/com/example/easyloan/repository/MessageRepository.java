package com.example.easyloan.repository;

import com.example.easyloan.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message save(Message messageDto);

    List<Message> findBySenderId(Long senderId);
    @Query("SELECT m.senderId, m FROM Message m")
    List<Object[]> findAllMessagesGroupedBySender();
}
