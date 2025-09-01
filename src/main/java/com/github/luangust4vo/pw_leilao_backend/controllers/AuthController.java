package com.github.luangust4vo.pw_leilao_backend.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PasswordResetRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PersonRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.UpdatePasswordRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody PersonRequestDTO person) {
        return authService.auth(person);
    }

    @PostMapping("/register")
    public void register(@RequestBody PersonRequestDTO person) {
        authService.register(person);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refresh(Principal principal) {
        return authService.refreshToken(principal.getName());
    }

    @GetMapping("/verify-account")
    public AuthResponseDTO verifyAccount(@RequestParam("code") String code) {
        return authService.verifyAccount(code);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        authService.requestPasswordReset(request);
    }

    @PostMapping("/verify-reset-code")
    @ResponseStatus(HttpStatus.OK)
    public void verifyResetCode(@RequestParam("code") String code) {
        authService.verifyPasswordResetCode(code);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@Valid @RequestBody UpdatePasswordRequestDTO request) {
        authService.updatePassword(request);
    }
}