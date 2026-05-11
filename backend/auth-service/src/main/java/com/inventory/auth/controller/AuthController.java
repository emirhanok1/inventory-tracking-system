package com.inventory.auth.controller;

import com.inventory.auth.dto.AuthResponse;
import com.inventory.auth.dto.LoginRequest;
import com.inventory.auth.dto.RegisterRequest;
import com.inventory.auth.service.AuthService;
import com.inventory.common.dto.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public GenericResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return GenericResponse.success("Kullanıcı başarıyla kaydedildi", response);
    }

    @PostMapping("/login")
    public GenericResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return GenericResponse.success("Giriş başarılı", response);
    }

    @PostMapping("/logout/{userId}")
    public GenericResponse<Void> logout(@PathVariable Long userId) {
        authService.logout(userId);
        return GenericResponse.success("Çıkış başarılı", null);
    }
}
