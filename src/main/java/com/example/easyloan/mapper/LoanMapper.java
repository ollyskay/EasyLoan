package com.example.easyloan.mapper;


import com.example.easyloan.dto.LoanOfferResponse;
import com.example.easyloan.model.LoanOffer;

public class LoanMapper {
    public static LoanOfferResponse mapToLoanOfferResponse(LoanOffer loanoffer, LoanOfferResponse loanOfferResponse){
        loanOfferResponse.setAmount (loanoffer.getOfferAmount());
        loanOfferResponse.setDate(loanoffer.getDate());
        loanOfferResponse.setPaymentMethod(loanoffer.getPaymentMethod());
        loanOfferResponse.setDescription(loanoffer.getDescription ());
        return loanOfferResponse;
    }
}
