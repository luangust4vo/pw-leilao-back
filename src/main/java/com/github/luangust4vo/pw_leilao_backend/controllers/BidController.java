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
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponse;
import com.github.luangust4vo.pw_leilao_backend.dto.BidRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.BidResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.BidService;

import jakarta.validation.Valid;

@RestController
public class BidController {
    @Autowired
    private BidService bidService;

    @GetMapping("/api/auctions/{auctionId}/bids")
    public ResponseEntity<Page<BidResponseDTO>> getBidsByAuction(
            @PathVariable("auctionId") Long auctionId,
            Pageable pageable) {
        return ResponseEntity.ok(bidService.findBidsByAuctionAsDTO(auctionId, pageable));
    }

    @PostMapping("/api/auctions/{auctionId}/bids")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<ApiResponse<Void>> placeBid(
            @PathVariable("auctionId") Long auctionId,
            @Valid @RequestBody BidRequestDTO bidDTO,
            Authentication authentication) {
        
        Person bidder = (Person) authentication.getPrincipal();
        bidService.placeBid(auctionId, bidDTO.getAmount(), bidder);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.sucesso("Lance realizado com sucesso!"));
    }

    @PutMapping("/api/auctions/{auctionId}/bids/{bidId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<ApiResponse<Void>> updateBid(
            @PathVariable("auctionId") Long auctionId,
            @PathVariable("bidId") Long bidId,
            @Valid @RequestBody BidRequestDTO bidDTO,
            Authentication authentication) {
        
        Person bidder = (Person) authentication.getPrincipal();
        bidService.updateBid(bidId, bidDTO.getAmount(), bidder);
        
        return ResponseEntity.ok(ApiResponse.sucesso("Lance atualizado com sucesso!"));
    }

    @DeleteMapping("/api/auctions/{auctionId}/bids/{bidId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteBid(
            @PathVariable("auctionId") Long auctionId,
            @PathVariable("bidId") Long bidId,
            Authentication authentication) {
        
        Person bidder = (Person) authentication.getPrincipal();
        bidService.deleteBid(bidId, bidder);
        
        return ResponseEntity.ok(ApiResponse.sucesso("Lance excluído com sucesso!"));
    }

    @GetMapping("/api/users/my-bids")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    public ResponseEntity<Page<BidResponseDTO>> getMyBids(Authentication authentication, Pageable pageable) {
        Person bidder = (Person) authentication.getPrincipal();
        return ResponseEntity.ok(bidService.findBidsByBidderAsDTO(bidder, pageable));
    }
}
