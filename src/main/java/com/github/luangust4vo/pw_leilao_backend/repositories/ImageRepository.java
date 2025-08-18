package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByAuction(Auction auction);
    List<Image> findByAuctionId(Long auctionId);
}
