package com.wampart.wampart.controller;

import com.wampart.wampart.dto.request.AdminCustomerRegistrationRequest;
import com.wampart.wampart.dto.response.AuthResponse;
import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.service.AuthService;
import com.wampart.wampart.service.SuperAdminService;
import com.wampart.wampart.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SuperAdminController {
    private final SuperAdminService superAdminService;
    private final UserService userService;
    private final AuthService authService;

    @PatchMapping("super-admin/user/{userId}/assign-admin")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<String> assignAdminRole(@PathVariable String userId) {
        superAdminService.assignAdminRole(userId);
        return ResponseEntity.ok("Admin role assigned successfully");
    }

    @PatchMapping("super-admin/user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> removeAdminRole(@PathVariable String userId) {
        superAdminService.revokeAdminRole(userId);
        return ResponseEntity.ok("Admin role removed successfully");
    }

    @GetMapping("super-admin/admins")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(superAdminService.getAllAdmins());
    }

    @GetMapping("admin/customers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        return ResponseEntity.ok(superAdminService.getAllCustomers());
    }

//    @GetMapping("admin/customers/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity <UserResponse> getCustomers(@PathVariable String id) {
//        return ResponseEntity.ok(superAdminService.getCustomerById(id));
//    }



//    @GetMapping("/admin/customers")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN' )")
//    public ResponseEntity<List<UserResponse>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//
//    }



    @GetMapping("/admin/customers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UserResponse> getUserByIdForAdmin(@PathVariable  String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @DeleteMapping("admin/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #id == authentication.principal.username")
    ResponseEntity<String> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PostMapping("/admin/users/create-account-for-customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    ResponseEntity<AuthResponse> createAccountForCustomer(@Valid @RequestBody AdminCustomerRegistrationRequest request) {
        return ResponseEntity.ok(authService.registerByAdmin(request));

    }



    @PatchMapping("admin/users/{userId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> deactivateUser(@PathVariable String userId) {
        superAdminService.deactivateUser(userId);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PatchMapping("/admin/users/{userId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable String userId) {
        superAdminService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully");
    }

    @PatchMapping("/admin/users/{userId}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> verifyUser(@PathVariable String userId) {
        superAdminService.verifyUser(userId);
        return ResponseEntity.ok("User verified successfully");
    }

    @PatchMapping("/admin/users/{userId}/unverify")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> unverifyUser(@PathVariable String userId) {
        superAdminService.verifyUser(userId);
        return ResponseEntity.ok("User unverified successfully");
    }



}
