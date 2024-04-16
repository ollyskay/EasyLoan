package com.example.easyloan.repository;


import com.example.easyloan.model.GovernmentID;
import com.example.easyloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GovernmentIDRepository extends JpaRepository<GovernmentID, Long> {
    Optional findByUser(User user);
}
