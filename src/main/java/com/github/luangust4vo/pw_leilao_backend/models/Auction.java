package com.github.luangust4vo.pw_leilao_backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false)
    @NotBlank(message = "{validation.auction.title.not-blank}")
    @Size(max = 200, message = "{validation.auction.title.size}")
    private String title;
    
    @Column(name = "description", nullable = false)
    @NotBlank(message = "{validation.auction.description.not-blank}")
    @Size(max = 500, message = "{validation.auction.description.size}")
    private String description;
    
    @Column(name = "detailed_description", columnDefinition = "TEXT")
    @Size(max = 2000, message = "{validation.auction.detailed-description.size}")
    private String detailedDescription;
    
    @Column(name = "start_date_time", nullable = false)
    @NotNull(message = "{validation.auction.start-date-time.not-null}")
    private LocalDateTime startDateTime;
    
    @Column(name = "end_date_time", nullable = false)
    @NotNull(message = "{validation.auction.end-date-time.not-null}")
    private LocalDateTime endDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "{validation.auction.status.not-null}")
    private AuctionStatus status = AuctionStatus.IN_REVIEW;
    
    @Column(name = "observation")
    @Size(max = 1000, message = "{validation.auction.observation.size}")
    private String observation;
    
    @Column(name = "increment_value", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "{validation.auction.increment-value.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.auction.increment-value.min}")
    private BigDecimal incrementValue;
    
    @Column(name = "minimum_bid", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "{validation.auction.minimum-bid.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.auction.minimum-bid.min}")
    private BigDecimal minimumBid;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "{validation.auction.category.not-null}")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Person seller;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
