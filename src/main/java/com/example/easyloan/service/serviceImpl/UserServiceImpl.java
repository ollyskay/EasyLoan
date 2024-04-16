package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.dto.ChangePasswordDto;
import com.example.easyloan.dto.EmailDTO;
import com.example.easyloan.dto.ResetPassDTO;
import com.example.easyloan.exception.BadRequestException;
import com.example.easyloan.exception.IncorrectOldPasswordException;
import com.example.easyloan.exception.PasswordMismatchException;
import com.example.easyloan.exception.UserNotFoundException;
import com.example.easyloan.model.ResetRequest;
import com.example.easyloan.model.User;
import com.example.easyloan.repository.ResetPasswordRepo;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.EmailService;
import com.example.easyloan.service.UserService;
import com.example.easyloan.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ResetPasswordRepo resetPasswordRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public void initiatePasswordRequest(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            ResetRequest request = new ResetRequest();
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime resetTokenExpiry = LocalDateTime.now().plusMinutes(10);

            request.setResetToken(resetToken);
            request.setResetTokenExpiry(resetTokenExpiry);
            request.setUser(userOptional.get());

            resetPasswordRepo.save(request);

            String resetPasswordLink = "http://localhost:3001/reset-password?token="+resetToken;

            EmailDTO emailDTO = EmailDTO.builder()
                    .recipient(userOptional.get().getUsername())
                    .subject("Password Reset")
                    .messageBody("Click the following link to reset your password: " + resetPasswordLink)
                    .build();
            emailService.sendEmailAlert(emailDTO);
        } else {
            throw new UserNotFoundException("User with email " + username + " not found", HttpStatus.NOT_FOUND);
        }
    }


    public String resetPassword(ResetPassDTO resetPassDTO) {
        Optional<ResetRequest> resetRequestOptional = resetPasswordRepo.findByResetToken(resetPassDTO.getResetToken());
        if (resetRequestOptional.isPresent()) {
            ResetRequest resetRequest = resetRequestOptional.get();
            if (resetRequest.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                return null;
            }

            Optional<User> userOptional = userRepository.findByUsername(resetRequest.getUser().getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!resetPassDTO.getNewPassword().equals(resetPassDTO.getConfirmPassword())) {
                    throw new BadRequestException("Passwords do not match", HttpStatus.BAD_REQUEST);
                }
                if (!PasswordValidator.isValid(resetPassDTO.getNewPassword())) {
                    throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
                }
                user.setPassword(passwordEncoder.encode(resetPassDTO.getNewPassword()));
                userRepository.save(user);

                resetRequest.setResetToken(null);
                resetRequest.setResetTokenExpiry(null);
                resetPasswordRepo.save(resetRequest);

                return "Your password successfully updated.";
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<String> changePassword(ChangePasswordDto changePasswordDto) {
        Optional<User> targetUser = userRepository.findById(changePasswordDto.getUserId());
        if(targetUser.isEmpty()){
            throw new UserNotFoundException("User not found",HttpStatus.NOT_FOUND);
        }
        User user = targetUser.get();
        if(!oldPasswordIsValid(user, changePasswordDto.getOldPassword())){
            throw new IncorrectOldPasswordException("Incorrect old password!");
        }
        if (!PasswordValidator.isValid(changePasswordDto.getNewPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if(!Objects.equals(changePasswordDto.getNewPassword(), changePasswordDto.getConfirmNewPassword())){
            throw new PasswordMismatchException("Password does not match!");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Override
    public boolean oldPasswordIsValid(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public User getUserById(Long senderId) {
        return null;
    }

}
