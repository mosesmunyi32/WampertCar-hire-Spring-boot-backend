package com.wampart.wampart.controller;


import com.wampart.wampart.dto.request.ChangePasswordRequest;
import com.wampart.wampart.dto.request.LoginRequest;
import com.wampart.wampart.dto.request.RegisterRequest;
import com.wampart.wampart.dto.response.AuthResponse;
import com.wampart.wampart.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request ){
        return ResponseEntity.ok(authService.login(request ));
    }

    @PostMapping("/security/change-password")
    public ResponseEntity<AuthResponse> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {

        return ResponseEntity.ok(
                authService.handleChangePassword(request)
        );
    }

}
