package com.example.easyloan.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FireBaseConfig {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("loanden-firebase-secretkey.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "Bank-app");
        return FirebaseMessaging.getInstance(app);
    }
}
