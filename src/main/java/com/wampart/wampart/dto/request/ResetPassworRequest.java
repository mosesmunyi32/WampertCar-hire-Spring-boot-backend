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

public class ResetPassworRequest {

    private String PhoneNumber;
    private String email;

    @NotBlank(message = "OTP is required")
    private String Otp;


    @NotBlank(message = "New password is required")
    private String newPassword;
}
