package com.example.easyloan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContactInfo extends BaseEntity {
    private String fileName;
    private String phoneNumber;
    @OneToOne
    private User user;
}
