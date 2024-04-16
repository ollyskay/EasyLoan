package com.example.easyloan.service;


import com.example.easyloan.dto.MessageDto;
import com.example.easyloan.exception.RecipientNotFoundException;
import com.example.easyloan.model.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Message createMessage(MessageDto messageDto) throws RecipientNotFoundException;

    void registerFcmToken(Long userId, String fcmToken);

    Map<Long, List<Message>> getAllMessagesGroupedBySender();
}
