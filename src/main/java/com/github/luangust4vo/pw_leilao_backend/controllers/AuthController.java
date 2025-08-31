package com.github.luangust4vo.pw_leilao_backend.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PersonRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.services.AuthService;

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
}