package com.example.easyloan.service;


import com.example.easyloan.dto.ApiResponse;
import com.example.easyloan.dto.LoanOfferDTO;
import com.example.easyloan.dto.LoanOfferResponse;
import com.example.easyloan.model.LoanOffer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LoanOfferService {
    ResponseEntity<ApiResponse<LoanOffer>> createLoanOffer(LoanOfferDTO loanOfferDTO);
    ApiResponse<List<LoanOfferResponse>> getLoanOffersByUsername();
}