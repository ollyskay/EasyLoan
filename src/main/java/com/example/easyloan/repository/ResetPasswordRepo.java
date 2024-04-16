package com.example.easyloan.repository;

import com.example.easyloan.model.ResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ResetPasswordRepo extends JpaRepository<ResetRequest, Long> {
    Optional<ResetRequest> findByResetToken(String resetToken);
}
