package com.example.easyloan.repository;


import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.model.LoanRequest;
import com.example.easyloan.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    Optional<LoanRequest> findByUserIdAndLoanStatus(Long userId, LoanStatus loanStatus);

    Optional<LoanRequest> findByReference(String reference);

    Optional<LoanRequest> findByUser(User user);

    List<LoanRequest> findByLoanStatus(@Param("loanStatus") LoanStatus loanStatus, Pageable pageable);


//    List<LoanRequest> findAll(String loanStatus, Pageable pageable);
}
