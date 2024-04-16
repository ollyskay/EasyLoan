package com.example.easyloan.repository;

import com.example.easyloan.model.Lender;
import com.example.easyloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LenderRepository extends JpaRepository<Lender, Long> {
    Optional findByUser(User user);
}
