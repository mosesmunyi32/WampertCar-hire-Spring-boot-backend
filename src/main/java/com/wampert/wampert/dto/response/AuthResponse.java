package com.wampert.wampert.dto.response;

import com.wampert.wampert.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isVerified;
    private Boolean isActive;
}
