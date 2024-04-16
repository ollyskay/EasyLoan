package com.example.easyloan.service;

import com.example.easyloan.dto.LendingOfferDto;
import com.example.easyloan.dto.LoanRequestDTO;
import com.example.easyloan.enums.LoanStatus;
import com.example.easyloan.model.Earning;
import com.example.easyloan.model.LoanRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
  String loanRequest(LoanRequestDTO loanRequestDTO);
  String acceptLoan(Long loanRequestId);

  List<LendingOfferDto> lendingOffers(Integer pageNo, Integer pageSize);

  Double getOutstandingBalance(Long userId, LoanStatus loanStatus);

    void updateLoanStatus(String reference, LoanStatus newStatus, LoanRequest loan);

  Double getLenderEarnings();

  List<Earning> getMonthlyEarningsByDate(LocalDate date);


  Page<LoanRequest> getAllLoanRequests(Integer pageNo, Integer pageSize);
}
