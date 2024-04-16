package com.example.easyloan.service;


import com.example.easyloan.dto.BorrowerKYCDto;
import com.example.easyloan.dto.KYCUpdateResponseDto;
import com.example.easyloan.dto.LenderKYCDto;

public interface KYCUpdateService {
    KYCUpdateResponseDto updateLenderKYC(LenderKYCDto lenderKYCDto);

    KYCUpdateResponseDto updateBorrowerKyc(BorrowerKYCDto borrowerKYCDto);
}
