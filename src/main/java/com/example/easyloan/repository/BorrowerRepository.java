package com.example.easyloan.repository;

import com.example.easyloan.model.Borrower;
import com.example.easyloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<?> findByUser(User user);
}
