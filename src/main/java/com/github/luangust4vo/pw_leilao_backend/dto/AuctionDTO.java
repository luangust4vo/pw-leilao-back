package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDTO {
    private Long id;
    private String title;
    private String description;
    private String detailedDescription;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private AuctionStatus status;
    private String observation;
    private BigDecimal incrementValue;
    private BigDecimal minimumBid;
    private CategoryInfo category;
    private SellerInfo seller;
    private List<BidResponseDTO> bids;
    private BidResponseDTO highestBid;
    private BigDecimal nextMinimumBid;
    private Long totalBids;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellerInfo {
        private Long id;
        private String name;
    }
}
