package com.example.easyloan.service;


import com.example.easyloan.dto.AuthenticationResponse;
import com.example.easyloan.dto.LoginDto;
import com.example.easyloan.dto.UserDTO;
import com.example.easyloan.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<UserResponseDTO> userReg(UserDTO userDTO);
    AuthenticationResponse loginUser(LoginDto loginDto);

    void logout();
}
