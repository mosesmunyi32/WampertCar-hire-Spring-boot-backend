package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.ForgotPasswordRequest;
import com.wampart.wampart.dto.request.ResetPassworRequest;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final WhatsAppService whatsAppService;
    private final PasswordEncoder passwordEncoder;


    //==========Helper Methods ==========//
    private UserEntity findUser(ForgotPasswordRequest request) {
        if(request.getPhoneNumber() != null ) {
            return userRepository.findByPhoneNumber(request.getPhoneNumber()).orElseThrow(()-> new ResourceNotFoundException("User not found with phone number: " + request.getPhoneNumber()));

        } else {
            return userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        }
    }

    private UserEntity findUserForReset(ResetPassworRequest request) {
        if(request.getPhoneNumber() != null ) {
            return userRepository.findByPhoneNumber(request.getPhoneNumber()).orElseThrow(()-> new ResourceNotFoundException("User not found with phone number: " + request.getPhoneNumber()));

        } else {
            return userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 1000000 + random.nextInt(9000000);
        return String.valueOf(otp);
    }

    private String buildOtpMessage(String firstName, String otp) {
        return "Hello " + firstName + ",\n\n" +
                "Your OTP for password reset is: " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Thank you,\n" +
                "WAMP Team";
    }







    //============Send OTP ==================//
   public String sendOtp(ForgotPasswordRequest request) {
       if(request.getPhoneNumber() == null && request.getEmail() == null ) {
           throw new RuntimeException("Please provide either email or phone number");
       }

       UserEntity user = findUser(request);

       String otp  = generateOtp();

       LocalDateTime otpExpiryTime = LocalDateTime.now().plusMinutes(10);


       user.setResetOtp(otp);
       user.setOtpExpiryTime(otpExpiryTime);
       userRepository.save(user);

       //send through whatsapp
       String message = buildOtpMessage(user.getFirstName(), otp);
       whatsAppService.sendWhatsAppMessage(user.getPhoneNumber(), message);

       log.info("OTP sent successfully to: " + user.getPhoneNumber());
       return "OTP sent successfully to your whats app number";


   }


   //========Reset Password  ===============

    public String resetPassword(ResetPassworRequest request) {
        if(request.getPhoneNumber() == null && request.getEmail() == null ) {
            throw new RuntimeException("Please provide either email or phone number");
        }

        UserEntity user = findUserForReset(request);

        if(user.getResetOtp() == null) {
            throw new RuntimeException("OTP has expired or is invalid");
        }

        if(LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            user.setResetOtp(null);
            user.setOtpExpiryTime(null);
            userRepository.save(user);
            throw new RuntimeException("OTP has expired. Please request a new OTP");
        }

        if(!user.getResetOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP, please try again");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setResetOtp(null);
        user.setOtpExpiryTime(null);

        userRepository.save(user);
        log.info("password reset successfully for user: " + user.getPhoneNumber());

        return "password reset successfully, please login with your new password";

    }

}
