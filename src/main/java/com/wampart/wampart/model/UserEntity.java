package com.wampart.wampart.model;


import com.wampart.wampart.enums.Gender;
import com.wampart.wampart.enums.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity implements UserDetails {
    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    @Min(value = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Indexed(unique = true)
    private String phoneNumber;


    private String alternativePhoneNumber;

    private LocalDate dateOfBirth;
    private Gender gender;
    private String county;
    private String city;
    private String profilePhoto;

    @Indexed(unique = true)
    private String idNumber;

    private String idBackPhoto;
    private String idFrontPhoto;
    private String driversLicenceNumber;
    private String isDriversLicenceValid;
    private Role role;
    private Boolean isVerified;
    private Boolean isActive;
    private String resetOtp;
    private LocalDateTime otpExpiryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority( "ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return isActive;
    }

}
