package com.example.easyloan.model;

import com.example.easyloan.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Message extends AuditBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Status status;
    private String content;


    @ManyToOne
    private User user;
}
