package com.wampart.wampart.controller;


import com.wampart.wampart.dto.request.UpdateUserRequest;
import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;



    @PatchMapping("users/profile/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER', 'SUPER_ADMIN') or #id == authentication.principal.username")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest userResponse) {
        return ResponseEntity.ok(userService.updateUser(id, userResponse));
    }


    @GetMapping("profile/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER', 'SUPER_ADMIN') or #id == authentication.principal.username")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }




}
