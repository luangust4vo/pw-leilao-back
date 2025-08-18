package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Bid;
import com.github.luangust4vo.pw_leilao_backend.models.Person;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionOrderByAmountDesc(Auction auction);
    Page<Bid> findByAuctionOrderByAmountDesc(Auction auction, Pageable pageable);
    
    List<Bid> findByAuctionOrderByBidTimeDesc(Auction auction);
    Page<Bid> findByAuctionOrderByBidTimeDesc(Auction auction, Pageable pageable);
    
    List<Bid> findByBidderOrderByBidTimeDesc(Person bidder);
    Page<Bid> findByBidderOrderByBidTimeDesc(Person bidder, Pageable pageable);
    
    @Query("SELECT b FROM Bid b WHERE b.auction = :auction ORDER BY b.amount DESC LIMIT 1")
    Optional<Bid> findHighestBidByAuction(@Param("auction") Auction auction);
    
    @Query("SELECT MAX(b.amount) FROM Bid b WHERE b.auction = :auction")
    Optional<BigDecimal> findMaxBidAmountByAuction(@Param("auction") Auction auction);
    
    long countByAuction(Auction auction);
    
    boolean existsByAuctionAndBidder(Auction auction, Person bidder);
}
