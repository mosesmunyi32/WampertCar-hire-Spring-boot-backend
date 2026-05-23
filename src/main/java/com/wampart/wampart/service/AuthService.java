package com.wampart.wampart.service;


import com.wampart.wampart.config.JwtService;
import com.wampart.wampart.dto.request.AdminCustomerRegistrationRequest;
import com.wampart.wampart.dto.request.ChangePasswordRequest;
import com.wampart.wampart.dto.request.LoginRequest;
import com.wampart.wampart.dto.request.RegisterRequest;
import com.wampart.wampart.dto.response.AuthResponse;
import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.enums.Role;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WhatsAppService whatsAppService;



    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));

    }

    public AuthResponse registerByAdmin(AdminCustomerRegistrationRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("email already exist");
        }

        if(userRepository.existsByIdNumber(request.getIdNumber())) {
            throw new RuntimeException("customer with that user ID number exists");
        }


        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .county(request.getCounty())
                .alternativePhoneNumber(request.getAlternativePhoneNumber())
                .phoneNumber(request.getPhoneNumber())
                .city(request.getCity())
                .idNumber(request.getIdNumber())
                .driversLicenceNumber(request.getDriversLicenceNumber())
                .role(Role.CUSTOMER)
                .isVerified(false)
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .isVerified(true)
                .isActive(true)
                .build();


    }







    public AuthResponse register(RegisterRequest request) {


        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("email already exist");
        }



        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .county(request.getCounty())
                .alternativePhoneNumber(request.getAlternativePhoneNumber())
                .phoneNumber(request.getPhoneNumber())
                .city(request.getCity())
                .idNumber(request.getIdNumber())
                .driversLicenceNumber(request.getDriversLicenceNumber())
                .role(Role.CUSTOMER)
                .isVerified(false)
                .isActive(true)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtService.generateToken(savedUser);



        try {
            whatsAppService.notifyAdminNewUser(
                    savedUser.getFirstName(),
                    savedUser.getPhoneNumber(),
                    savedUser.getEmail(),
                    savedUser.getLastName()
            );
        } catch (Exception e) {
            log.warn("failed to send whatsapp notification: {}", e.getMessage() );
        }








        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .isVerified(savedUser.getIsVerified())
                .isActive(savedUser.getIsActive())
                .build();
    }


    public AuthResponse handleChangePassword(ChangePasswordRequest request) {
        UserEntity currentUser = getCurrentUser();
        if(!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new RuntimeException("old password is incorrect");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        UserEntity updatedUser = userRepository.save(currentUser);
        return AuthResponse.builder()
                .token(jwtService.generateToken(updatedUser))
                .id(updatedUser.getId())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .isVerified(updatedUser.getIsVerified())
                .isActive(updatedUser.getIsActive())
                .build();


    }





    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("password or email is incorrect"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .build();



    }
}
