package com.github.luangust4vo.pw_leilao_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponse;
import com.github.luangust4vo.pw_leilao_backend.dto.AuctionDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;
import com.github.luangust4vo.pw_leilao_backend.services.AuctionService;
import com.github.luangust4vo.pw_leilao_backend.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public")
    public ResponseEntity<Page<Auction>> findOpenAuctions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            Pageable pageable) {
        
        if (categoryId != null) {
            Category category = categoryService.findById(categoryId);
            return ResponseEntity.ok(auctionService.findByCategoryAndStatus(category, AuctionStatus.OPENED, pageable));
        }
        
        if (title != null || description != null) {
            return ResponseEntity.ok(auctionService.findOpenAuctionsWithFilters(title, description, pageable));
        }
        
        return ResponseEntity.ok(auctionService.findOpenAuctions(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<AuctionDTO> findOpenAuctionById(@PathVariable("id") Long id) {
        try {
            AuctionDTO auction = auctionService.findOpenAuctionByIdWithBids(id);
            return ResponseEntity.ok(auction);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Page<Auction>> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) AuctionStatus status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long sellerId,
            Pageable pageable) {
        
        if (categoryId != null || status != null || title != null || description != null || sellerId != null) {
            return ResponseEntity.ok(auctionService.findWithFilters(categoryId, status, title, description, sellerId, pageable));
        }
        
        return ResponseEntity.ok(auctionService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<AuctionDTO> findById(@PathVariable("id") Long id) {
        AuctionDTO auction = auctionService.findByIdWithBids(id);
        return ResponseEntity.ok(auction);
    }

    @GetMapping("/my-auctions")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<Auction>> findMyAuctions(Authentication authentication, Pageable pageable) {
        Person seller = (Person) authentication.getPrincipal();
        return ResponseEntity.ok(auctionService.findBySeller(seller, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody Auction auction, Authentication authentication) {
        Person seller = (Person) authentication.getPrincipal();
        auction.setSeller(seller);
        auctionService.create(auction);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.sucesso("Leilão criado com sucesso! Aguardando aprovação do administrador."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable("id") Long id, 
            @Valid @RequestBody Auction auction,
            Authentication authentication) {
        
        Auction existingAuction = auctionService.findById(id);
        Person currentUser = (Person) authentication.getPrincipal();
        
        boolean isAdmin = currentUser.getPersonProfiles().stream()
            .anyMatch(pp -> pp.getProfile().getType().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !existingAuction.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.erro("Você não tem permissão para editar este leilão."));
        }
        
        auctionService.update(id, auction);
        return ResponseEntity.ok(ApiResponse.sucesso("Leilão atualizado com sucesso!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id, Authentication authentication) {
        Auction existingAuction = auctionService.findById(id);
        Person currentUser = (Person) authentication.getPrincipal();
        
        boolean isAdmin = currentUser.getPersonProfiles().stream()
            .anyMatch(pp -> pp.getProfile().getType().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !existingAuction.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.erro("Você não tem permissão para excluir este leilão."));
        }
        
        auctionService.delete(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Leilão excluído com sucesso!"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveAuction(@PathVariable("id") Long id) {
        auctionService.approveAuction(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Leilão aprovado e aberto para lances!"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectAuction(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String observation) {
        auctionService.rejectAuction(id, observation);
        return ResponseEntity.ok(ApiResponse.sucesso("Leilão rejeitado."));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> closeAuction(@PathVariable("id") Long id) {
        auctionService.closeAuction(id);
        return ResponseEntity.ok(ApiResponse.sucesso("Leilão encerrado com sucesso!"));
    }
}
