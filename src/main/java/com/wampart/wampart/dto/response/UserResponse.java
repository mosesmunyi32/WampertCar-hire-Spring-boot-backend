package com.wampart.wampart.dto.response;


import com.wampart.wampart.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String county;
    private String city;
    private String profilePhoto;
    private String idBackPhoto;
    private String idFrontPhoto;
    private String idNumber;
    private String isDriversLicenceValid;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
