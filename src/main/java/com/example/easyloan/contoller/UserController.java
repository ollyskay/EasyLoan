package com.example.easyloan.contoller;

import com.example.easyloan.dto.ChangePasswordDto;
import com.example.easyloan.dto.ResetPassDTO;
import com.example.easyloan.service.AuthService;
import com.example.easyloan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }

    @PostMapping("/initiate-password-request")
    public ResponseEntity<String> initiateRequest(@RequestParam String username){
        userService.initiatePasswordRequest(username);
        return ResponseEntity.ok().body("Instructions of how to reset your password has been sent to your email");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassDTO resetPassDTO) {
        if (userService.resetPassword(resetPassDTO) != null) {
            return ResponseEntity.ok().body("You've successfully reset password");
        } else {
            return new ResponseEntity<>("Invalid reset token or token has expired", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        return  userService.changePassword(changePasswordDto);
    }
}
