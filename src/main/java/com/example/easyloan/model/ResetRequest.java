package com.example.easyloan.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ResetRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}