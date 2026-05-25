package com.wampart.wampart.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String idNumber;

    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "password is required" )
    private String password;
}
