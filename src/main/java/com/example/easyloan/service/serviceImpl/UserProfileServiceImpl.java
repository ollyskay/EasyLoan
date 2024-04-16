package com.example.easyloan.service.serviceImpl;


import com.example.easyloan.dto.EditUserProfileRequestDto;
import com.example.easyloan.dto.EditUserProfileResponseDto;
import com.example.easyloan.model.User;
import com.example.easyloan.repository.UserRepository;
import com.example.easyloan.service.BorrowerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service

public class UserProfileServiceImpl implements BorrowerService {
        private final UserRepository userRepository;
    @Override
    @Transactional
    public EditUserProfileResponseDto editUserProfile(EditUserProfileRequestDto editUserProfileRequestDto){
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                               .orElseThrow(()-> new RuntimeException("USER NOT FOUND"));
                EditUserProfileResponseDto editUserProfileResponseDto = new EditUserProfileResponseDto();
                user.setLastName(editUserProfileRequestDto.getLastName());
                user.setFirstName(editUserProfileRequestDto.getFirstName());
                userRepository.save(user);
                editUserProfileResponseDto.setFirstName(user.getFirstName());
               editUserProfileResponseDto.setLastName(user.getLastName());
               return editUserProfileResponseDto;
            }


}
