package com.example.easyloan.dto;

import com.example.easyloan.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthenticationResponse {
    @JsonProperty("token")
    private String token;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private Integer accountStatus;
}
