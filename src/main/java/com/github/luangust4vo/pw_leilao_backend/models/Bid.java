package com.github.luangust4vo.pw_leilao_backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "bid")
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    @NotNull(message = "{validation.bid.auction.not-null}")
    private Auction auction;
    
    @ManyToOne
    @JoinColumn(name = "bidder_id", nullable = false)
    private Person bidder;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "{validation.bid.amount.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.bid.amount.min}")
    private BigDecimal amount;
    
    @Column(name = "bid_time", nullable = false)
    private LocalDateTime bidTime;
    
    @PrePersist
    protected void onCreate() {
        this.bidTime = LocalDateTime.now();
    }
}
