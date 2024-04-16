package com.example.easyloan.contoller;

import com.example.easyloan.dto.FcmTokenRequest;
import com.example.easyloan.dto.MessageDto;
import com.example.easyloan.exception.RecipientNotFoundException;
import com.example.easyloan.model.Message;
import com.example.easyloan.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {
        try {
            String sentMessage = String.valueOf(messageService.createMessage(messageDto));
            return ResponseEntity.ok(sentMessage);
        } catch (RecipientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/save-fcm-token")
    public ResponseEntity<?> saveFcmToken(@RequestBody FcmTokenRequest fcmTokenRequest) {
        try {
            messageService.registerFcmToken(fcmTokenRequest.getUserId(), fcmTokenRequest.getFcmToken());
            return ResponseEntity.ok("FCM token saved successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all-messages-by-sender")
    public ResponseEntity<Map<Long, List<Message>>> getAllMessagesGroupedBySender() {
        Map<Long, List<Message>> messagesBySender = messageService.getAllMessagesGroupedBySender();
        return ResponseEntity.ok(messagesBySender);
    }
}

