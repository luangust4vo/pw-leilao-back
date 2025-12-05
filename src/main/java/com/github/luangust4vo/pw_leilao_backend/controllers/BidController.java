package com.github.luangust4vo.pw_leilao_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.BidRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.BidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    @Autowired
    private BidService bidService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Void>> create(
            @AuthenticationPrincipal Person user,
            @Valid @RequestBody BidRequestDTO dto) {
        
        bidService.create(dto, user);
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Lance realizado com sucesso!"));
    }
}