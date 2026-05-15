package com.wampart.wampart.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    private String city;
    private String county;
    private String phoneNumber;
    private String alternativePhoneNumber;
    @Valid
    private String email;
    private String profilePhoto;




}
