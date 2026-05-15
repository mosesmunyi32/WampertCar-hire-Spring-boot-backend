package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerRegistrationRequest {

    private String phoneNumber;
    @NotBlank(message = "first name is required")
    private String firstName;

    @NotBlank(message = "last name is required")
    private String lastName;

    @NotBlank(message = " email is required")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters long")

    @Value("${app.security.default-password}")
    private String password ;


    @NotNull(message ="Date of birth is required" )
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String county;
    private String city;

    @NotBlank(message = "Id number is required")
    private String idNumber;

    private String driversLicenceNumber;

    private String alternativePhoneNumber;

    private List<String> idImages = new ArrayList<>();

}
