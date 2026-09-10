package com.wampert.wampert.controller;

import com.wampert.wampert.dto.response.AdminCarResponse;
import com.wampert.wampert.dto.response.UserResponse;
import com.wampert.wampert.model.UserEntity;
import com.wampert.wampert.service.CarService;
import com.wampert.wampert.service.FileUploadService;
import com.wampert.wampert.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;
    private final CarService carService;
    private final UserService userService;

    //===========Car Images===========

    @PostMapping("/admin/cars/{carId}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminCarResponse> postCarImages(@PathVariable String carId, @RequestParam("files") List<MultipartFile> files ) {
        List<String> imageUrls = new ArrayList<>( fileUploadService.uploadMultipleFiles(files, "cars"));

        return ResponseEntity.ok(carService.addCarImages(carId, imageUrls));

    }


    @PutMapping("/admin/cars/{carId}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminCarResponse> uploadCarImage(@PathVariable String carId, @RequestParam("files") List<MultipartFile> files) {

        // Upload to local storage
       List<String> imageUrls = new ArrayList<>( fileUploadService.uploadMultipleFiles(files, "cars"));

       // Save the URLs to car
       return ResponseEntity.ok( carService.addCarImages(carId, imageUrls));

    }

    @DeleteMapping("/admin/cars/{carId}/images")
    @PreAuthorize("hasAnyRole('ADMIN' ,'SUPER_ADMIN')")
    public ResponseEntity<String> deleteCarImage(@PathVariable String carId, @RequestParam("imageUrl") String imageUrl ) {
        fileUploadService.deleteFile(imageUrl);
        carService.removeCarImage(carId, imageUrl);
        return ResponseEntity.ok("Car image deleted successfully");
    }


    //=================User Photos ================

    @PostMapping("/users/{userId}/profile-photo")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN' )")
    public ResponseEntity<String> uploadProfilePhoto (@PathVariable String userId, @RequestParam("file") MultipartFile file) {
        String photoUrl = fileUploadService.uploadFile(file, "profiles");

        userService.uploadProfilePhoto(userId, photoUrl);
        return ResponseEntity.ok("Profile photo uploaded successfully");

    }

    @PostMapping("/users/{userId}/id-photos")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserResponse> uploadIdPhoto(@PathVariable String userId, @RequestParam("frontPhoto") MultipartFile frontPhoto, @RequestParam("backPhoto") MultipartFile backPhoto ) {
        String frontPhotoUrl = fileUploadService.uploadFile(frontPhoto, "ids");
        String backPhotoUrl = fileUploadService.uploadFile(backPhoto, "ids");

        UserResponse savedUser = userService.updateIdPhotos(userId, frontPhotoUrl, backPhotoUrl);
        return ResponseEntity.ok(savedUser);

    }


    @PutMapping("/users/{userId}/profile-photo")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserResponse> updateProfilePhoto(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
        String photoUrl = fileUploadService.uploadFile(file, "profiles");

       UserResponse updatedUser = userService.updateProfilePhoto(userId, photoUrl);

        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/admin/{bookingId}/damage-photos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<String>> uploadDamagePhotos(@PathVariable String bookingId, @RequestParam("files") List<MultipartFile> files) {
        List<String> uploadUrls = fileUploadService.uploadMultipleFiles(files, "damages");
        return ResponseEntity.ok(uploadUrls);

    }

}
