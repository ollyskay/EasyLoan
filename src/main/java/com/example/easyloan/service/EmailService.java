package com.example.easyloan.service;


import com.example.easyloan.dto.EmailDTO;

public interface EmailService {
    void sendEmailAlert(EmailDTO emailDTO);
}
