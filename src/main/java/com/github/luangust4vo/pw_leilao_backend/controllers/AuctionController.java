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
    public ResponseEntity<Page<Auction>> findOpenAuctions(Pageable pageable) {
        return ResponseEntity.ok(auctionService.findOpenAuctions(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Auction> findOpenAuctionById(@PathVariable("id") Long id) {
        Auction auction = auctionService.findById(id);
        if (auction.getStatus() == AuctionStatus.OPENED) {
            return ResponseEntity.ok(auction);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<Page<Auction>> findOpenAuctionsByCategory(
            @PathVariable("categoryId") Long categoryId, 
            Pageable pageable) {
        Category category = categoryService.findById(categoryId);
        return ResponseEntity.ok(auctionService.findByCategoryAndStatus(category, AuctionStatus.OPENED, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<Page<Auction>> findAll(Pageable pageable) {
        return ResponseEntity.ok(auctionService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<Auction> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(auctionService.findById(id));
    }

    @GetMapping("/my-auctions")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<Auction>> findMyAuctions(Authentication authentication, Pageable pageable) {
        Person seller = (Person) authentication.getPrincipal();
        return ResponseEntity.ok(auctionService.findBySeller(seller, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Auction> create(@Valid @RequestBody Auction auction, Authentication authentication) {
        Person seller = (Person) authentication.getPrincipal();
        auction.setSeller(seller);
        return ResponseEntity.ok(auctionService.create(auction));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Auction> update(
            @PathVariable("id") Long id, 
            @Valid @RequestBody Auction auction,
            Authentication authentication) {
        
        Auction existingAuction = auctionService.findById(id);
        Person currentUser = (Person) authentication.getPrincipal();
        
        if (!existingAuction.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(auctionService.update(id, auction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Authentication authentication) {
        Auction existingAuction = auctionService.findById(id);
        Person currentUser = (Person) authentication.getPrincipal();
        
        if (!existingAuction.getSeller().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        auctionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auction> approveAuction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(auctionService.approveAuction(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auction> rejectAuction(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String observation) {
        return ResponseEntity.ok(auctionService.rejectAuction(id, observation));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auction> closeAuction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(auctionService.closeAuction(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Auction>> findByStatus(@PathVariable("status") AuctionStatus status, Pageable pageable) {
        return ResponseEntity.ok(auctionService.findByStatus(status, pageable));
    }
}
