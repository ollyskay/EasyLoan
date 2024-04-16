package com.example.easyloan.repository;

import com.example.easyloan.model.LoanRepaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepaymentHistoryRepository extends JpaRepository<LoanRepaymentHistory, Long> {
}
