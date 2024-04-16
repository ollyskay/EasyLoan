package com.example.easyloan.service;


import com.example.easyloan.dto.EditUserProfileRequestDto;
import com.example.easyloan.dto.EditUserProfileResponseDto;

public interface BorrowerService {
    EditUserProfileResponseDto editUserProfile(EditUserProfileRequestDto editUserProfileRequestDto);
}
