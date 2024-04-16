package com.example.easyloan.repository;

import com.example.easyloan.model.ContactInfo;
import com.example.easyloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    Optional findByUser(User user);
}
