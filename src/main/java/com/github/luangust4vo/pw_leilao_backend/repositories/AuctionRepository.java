package com.github.luangust4vo.pw_leilao_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
    
    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId")
    Page<Auction> findBySellerId(Long sellerId, Pageable pageable);
}