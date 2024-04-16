package com.example.easyloan.service;



import com.example.easyloan.dto.ChangePasswordDto;
import com.example.easyloan.dto.ResetPassDTO;
import com.example.easyloan.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    void initiatePasswordRequest(String email);
    String resetPassword(ResetPassDTO resetPassDTO);
    ResponseEntity<String> changePassword(ChangePasswordDto changePasswordDto);
    boolean oldPasswordIsValid(User user, String oldPassword);

    User getUserById(Long senderId);
}
