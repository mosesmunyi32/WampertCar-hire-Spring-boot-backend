package com.wampart.wampart.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ResetPasswordRequest {

    private String phoneNumber;
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;


    @NotBlank(message = "New password is required")
    private String newPassword;
}
