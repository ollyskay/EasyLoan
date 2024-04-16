package com.example.easyloan.contoller;


import com.example.easyloan.dto.EditUserProfileRequestDto;
import com.example.easyloan.dto.EditUserProfileResponseDto;
import com.example.easyloan.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final BorrowerService borrowerService;
    @PostMapping("/edit/profile")
    public ResponseEntity<EditUserProfileResponseDto> editUserProfile(@RequestBody EditUserProfileRequestDto editUserProfileRequestDto){
                    EditUserProfileResponseDto responseDto = borrowerService.editUserProfile(editUserProfileRequestDto);
                   return  new ResponseEntity<>(responseDto, HttpStatus.CREATED);
                }

    }
