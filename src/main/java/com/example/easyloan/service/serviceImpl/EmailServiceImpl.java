package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.dto.EmailDTO;
import com.example.easyloan.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor

public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private String senderName = "EasyLend";

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDTO emailDTO) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            var mailMessage = new MimeMessageHelper(message);
            mailMessage.setFrom(senderEmail,senderName);
            mailMessage.setTo(emailDTO.getRecipient());
            mailMessage.setSubject(emailDTO.getSubject());
            mailMessage.setText(emailDTO.getMessageBody(), true);
            javaMailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendEmail(String toEmail, String CONTENT, String SUBJECT) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(CONTENT);
        javaMailSender.send(mailMessage);
    }
}
