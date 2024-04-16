package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.dto.MessageDto;
import com.example.easyloan.exception.RecipientNotFoundException;
import com.example.easyloan.model.Message;
import com.example.easyloan.model.MessageMapper;
import com.example.easyloan.model.User;
import com.example.easyloan.repository.MessageRepository;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.MessageService;
import com.example.easyloan.service.UserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MessageMapper messageMapper;
    private final EmailServiceImpl emailService;

    @Autowired
    public MessageServiceImpl(
            UserRepository userRepository,
            FcmService fcmService,
            MessageRepository messageRepository,
            UserService userService,
            MessageMapper messageMapper,
            EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.fcmService = fcmService;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.messageMapper = messageMapper;
        this.emailService = emailService;
    }

    @Override
    public Message createMessage(MessageDto messageDto) throws RecipientNotFoundException {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RecipientNotFoundException("Sender not found"));

        User receiverId = userRepository.findById(messageDto.getReceiverId())
                .orElseThrow(() -> new RecipientNotFoundException("Recipient not found"));

        // Create and save the message
        Message message = messageMapper.convertToEntity(messageDto);
        message.setSenderId(sender.getId());
        message.setReceiverId(receiverId.getId());

        Message savedMessage = messageRepository.save(message);

        emailService.sendEmail(receiverId.getUsername(), "you are having a new message from "
                + receiverId.getFirstName(), "you have a new message");
        // Send push notifications
        sendPushNotifications(savedMessage);

        return savedMessage;
    }

    private void sendPushNotifications(Message message) {
        Long senderId = message.getSenderId();
        User participant = userService.getUserById(senderId);

        if (participant != null) {
            String fcmToken = participant.getFcmToken();
            if (fcmToken != null && !fcmToken.isEmpty()) {
                try {
                    fcmService.sendNotification(fcmToken, "New Message", "You have a new message");
                } catch (FirebaseMessagingException e) {
                }
            }
        }
    }
    @Override
    public void registerFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    public Map<Long, List<Message>> getAllMessagesGroupedBySender() {
        List<Object[]> result = messageRepository.findAllMessagesGroupedBySender();
        Map<Long, List<Message>> messagesBySender = new HashMap<>();

        for (Object[] row : result) {
            Long senderId = (Long) row[0];
            Message message = (Message) row[1];

            messagesBySender.computeIfAbsent(senderId, k -> new ArrayList<>()).add(message);
        }

        return messagesBySender;
    }
}
