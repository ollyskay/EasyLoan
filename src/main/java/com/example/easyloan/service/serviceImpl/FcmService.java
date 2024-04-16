package com.example.easyloan.service.serviceImpl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FcmService {
    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        com.google.firebase.messaging.Message message = Message.builder()
                .putData("Update of your account", title)
                .putData("Please update your account before it is too late", body)
                .setToken(token)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
