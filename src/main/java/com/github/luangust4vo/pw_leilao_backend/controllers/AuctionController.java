package com.github.luangust4vo.pw_leilao_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AuctionRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AuctionResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.AuctionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping("/me")
    public ResponseEntity<Page<AuctionResponseDTO>> findAllByLoggedUser(
            @AuthenticationPrincipal Person loggedUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(auctionService.findAllBySeller(loggedUser, pageable));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<AuctionResponseDTO>> findAll(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(auctionService.findAllPublic(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<AuctionResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuctionResponseDTO> create(
            @AuthenticationPrincipal Person loggedUser,
            @Valid @RequestBody AuctionRequestDTO dto) {
        return ResponseEntity.ok(auctionService.create(dto, loggedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuctionResponseDTO> update(
            @PathVariable Long id,
            @AuthenticationPrincipal Person loggedUser,
            @Valid @RequestBody AuctionRequestDTO dto) {
        return ResponseEntity.ok(auctionService.update(id, dto, loggedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Person loggedUser) {
        auctionService.delete(id, loggedUser);
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Leilão excluído com sucesso."));
    }
}