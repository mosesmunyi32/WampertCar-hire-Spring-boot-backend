package com.wampart.wampart.service;

import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.enums.Role;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final UserRepository userRepository;

    //Assign Admins roles
    public String assignAdminRole(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(user.getRole() == Role.ADMIN ) {
            throw new RuntimeException("User already has admin role");
        }

        if(user.getRole() == Role.SUPER_ADMIN ) {
            throw new RuntimeException("User already has super admin role");
        }

        user.setRole(Role.ADMIN);
        UserEntity updatedUser = userRepository.save(user);
        return "updated " + updatedUser.getFirstName() + " role to admin" ;
    }

    public String revokeAdminRole(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(user.getRole() != Role.ADMIN ) {
            throw new RuntimeException("User is not an admin");
        }

        user.setIsActive(false);
        UserEntity updatedUser = userRepository.save(user);
        return "Deactivated" + updatedUser.getFirstName() ;
    }

    public List<UserResponse> getAllAdmins() {
        return userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(this::mapToUserResponse )
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllCustomers() {
        return userRepository.findByRole(Role.CUSTOMER)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

    }

    public UserResponse getCustomerById(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }





    public UserResponse activateUser(String id){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(user.getIsActive()) {
            throw new RuntimeException("User account is already activated");
        }
        user.setIsActive(true);
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    public UserResponse deactivateUser(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(user.getRole() == Role.SUPER_ADMIN) {
            throw new RuntimeException("Super admin cannot be deactivated");
        }
        if (!user.getIsActive()) {
            throw new RuntimeException(
                    "User account is already deactivated");
        }

        user.setIsActive(false);
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }



    public UserResponse verifyUser(String id){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));

        if(user.getRole() == Role.SUPER_ADMIN) {
            throw new RuntimeException("Super admin cannot be deactivated");
        }
        if (!user.getIsActive()) {
            throw new RuntimeException(
                    "User account is already verified");
        }

        user.setIsVerified(!user.getIsVerified());
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    public UserResponse unverifyUser(String id){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        if(user.getRole() == Role.SUPER_ADMIN) {
            throw new RuntimeException("Super admin cannot be deactivated");
        }
        if (!user.getIsActive()) {
            throw new RuntimeException(
                    "User account is already verified");
        }


        user.setIsVerified(false);
        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }






    //Mapping Method
    private UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .county(user.getCounty())
                .city(user.getCity())
                .isDriversLicenceValid(user.getIsDriversLicenceValid())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
