package com.example.easyloan.repository;

import com.example.easyloan.dto.LendingOfferDto;
import com.example.easyloan.model.LoanOffer;
import com.example.easyloan.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanOfferRepository extends JpaRepository<LoanOffer, Long> {
    List<LendingOfferDto> findAllByOfferStatus(String status, Pageable pageable);

    List<LoanOffer> findByUser(User user);

}
