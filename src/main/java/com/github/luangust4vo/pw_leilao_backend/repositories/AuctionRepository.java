package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Category;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStatus(AuctionStatus status);
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
    
    List<Auction> findByCategory(Category category);
    Page<Auction> findByCategory(Category category, Pageable pageable);
    
    List<Auction> findBySeller(Person seller);
    Page<Auction> findBySeller(Person seller, Pageable pageable);
    
    @Query("SELECT a FROM Auction a WHERE a.status = 'OPENED' ORDER BY a.endDateTime ASC")
    List<Auction> findOpenAuctions();
    
    @Query("SELECT a FROM Auction a WHERE a.status = 'OPENED' ORDER BY a.endDateTime ASC")
    Page<Auction> findOpenAuctions(Pageable pageable);
    
    @Query("SELECT a FROM Auction a WHERE a.category = :category AND a.status = :status")
    Page<Auction> findByCategoryAndStatus(@Param("category") Category category, 
                                         @Param("status") AuctionStatus status, 
                                         Pageable pageable);
}
