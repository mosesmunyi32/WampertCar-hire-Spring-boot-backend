package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.UpdateUserRequest;
import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }


    public UserResponse getUserById(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+ id));
        return mapToUserResponse(user);
    }

    public UserResponse updateUser(String id, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

       if(request.getCity() != null ) user.setCity(request.getCity());
       if(request.getCounty() != null ) user.setCounty(request.getCounty());
       if(request.getCity() != null ) user.setCity(request.getCity());
       if(request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
       if(request.getAlternativePhoneNumber() != null ) user.setAlternativePhoneNumber(request.getAlternativePhoneNumber());
       if(request.getEmail() != null ) user.setEmail(request.getEmail());

       UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);

    }


    public String deleteUser(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        return "User deleted successfully";
    }

    public String uploadProfilePhoto(String id, String imageUrl) {
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        user.setProfilePhoto(imageUrl);
        userRepository.save(user);
        return "Profile photo uploaded successfully";
    }

    public UserResponse updateProfilePhoto(String id, String imageUrl) {
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if(user.getProfilePhoto() != null) {
            try{
                fileUploadService.deleteFile(user.getProfilePhoto());

            } catch (Exception e) {
             throw new RuntimeException("Failed to delete old profile photo");

            }
        }

        user.setProfilePhoto(imageUrl);
       UserEntity savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public UserResponse updateIdPhotos(String id, String frontPhoto, String backPhoto) {
       UserEntity user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(user.getIdFrontPhoto() != null && user.getIdBackPhoto() != null ) {
            try{
                fileUploadService.deleteFile(user.getIdFrontPhoto());
                fileUploadService.deleteFile(user.getIdBackPhoto());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        if(user.getIdFrontPhoto() != null) {
            try{
                fileUploadService.deleteFile(user.getIdFrontPhoto());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(user.getIdBackPhoto() != null) {
            try{
                fileUploadService.deleteFile(user.getIdBackPhoto());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

       user.setIdFrontPhoto(frontPhoto);
        user.setIdBackPhoto(backPhoto);
        UserEntity savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);

    }





    private UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .alternativePhoneNumber(user.getAlternativePhoneNumber())
                .profilePhoto(user.getProfilePhoto())
                .dateOfBirth(user.getDateOfBirth())
                .idBackPhoto(user.getIdBackPhoto())
                .idFrontPhoto(user.getIdFrontPhoto())
                .idNumber(user.getIdNumber())
                .gender(user.getGender())
                .county(user.getCounty())
                .city(user.getCity())
                .isDriversLicenceValid(user.getIsDriversLicenceValid())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    }


