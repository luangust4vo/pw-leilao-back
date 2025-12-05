package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.luangust4vo.pw_leilao_backend.models.Bid;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);
}