package com.example.easyloan.service.serviceImpl;

import com.example.easyloan.configuration.JwtService;
import com.example.easyloan.dto.*;
import com.example.easyloan.exception.BadRequestException;
import com.example.easyloan.model.Token;
import com.example.easyloan.model.User;
import com.example.easyloan.repository.TokenRepository;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.AuthService;
import com.example.easyloan.util.MailMessages;
import com.example.easyloan.validation.EmailValidator;
import com.example.easyloan.validation.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j


public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest request;
    private final EmailServiceImpl emailService;




    private static String activeProfile;

    @Value("${spring.profiles.active:}")
    public void setActiveProfile(String activeProfileValue) {
        activeProfile = activeProfileValue;
    }

    @Override
    public ResponseEntity<UserResponseDTO> userReg(UserDTO userDTO) {

        if (!EmailValidator.isValid(userDTO.getUsername())) {
            throw new BadRequestException("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByUsernameIgnoreCase(userDTO.getUsername()).isPresent()) {
            throw new BadRequestException("User already exists", HttpStatus.BAD_REQUEST);
        }

        if (!PasswordValidator.isValid(userDTO.getPassword())) {
            throw new BadRequestException("Invalid password format", HttpStatus.BAD_REQUEST);

        }

        User user = map2Entity(userDTO);
        user.setAccountStatus(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(userDTO.getRole());

        User user1 = userRepository.save(user);
        String message= MailMessages.verifyEmail(user1.getFirstName());

        var emailDto = EmailDTO.builder()
                .recipient(user1.getUsername())
                .subject("VERIFICATION EMAIL")
                .messageBody(message)
                .build();
        emailService.sendEmailAlert(emailDto);

        UserResponseDTO response = UserResponseDTO.builder()
                .message("User created successfully")
                .httpStatus(HttpStatus.CREATED)
                .firstName(user1.getFirstName())
                .email(user1.getUsername())
                .id(user1.getId())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    public static User map2Entity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    @Override
    public AuthenticationResponse loginUser(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        User users1 = userRepository.findByUsernameIgnoreCase(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDto.getUsername()));
        String jwtToken = jwtService.generateToken(users1);
        revokeAllToken(users1);

        saveUserToken(users1, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .username(users1.getUsername())
                .firstName(users1.getFirstName())
                .lastName(users1.getLastName())
                .role(users1.getRole())
                .accountStatus(users1.getAccountStatus())
                .build();
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails= (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByUsernameIgnoreCase(email).get();
            System.out.println(user);
            revokeAllToken(user);
        }
        SecurityContextHolder.clearContext();
    }


    private void revokeAllToken(User user) {
        List<Token> tokenList = tokenRepository.findAllValidTokenByUser(user.getId());
        if (tokenList.isEmpty()) {
            return;
        }
        for (Token token : tokenList) {
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.saveAll(tokenList);
        }
    }

    private void saveUserToken(User savedUser, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .users(savedUser)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);

    }
}



