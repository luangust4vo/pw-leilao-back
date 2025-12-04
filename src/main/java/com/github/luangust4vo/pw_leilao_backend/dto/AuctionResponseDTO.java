package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.github.luangust4vo.pw_leilao_backend.models.Auction;
import com.github.luangust4vo.pw_leilao_backend.models.Image;
import com.github.luangust4vo.pw_leilao_backend.models.enums.AuctionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String detailedDescription;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private AuctionStatus status;
    private BigDecimal incrementValue;
    private BigDecimal minimumBid;
    private String categoryName;
    private Long categoryId;
    private String sellerName;
    private Long sellerId;
    private List<String> imageUrls;
    private String coverImage;

    public static AuctionResponseDTO fromEntity(Auction auction) {
        AuctionResponseDTO dto = new AuctionResponseDTO();
        dto.setId(auction.getId());
        dto.setTitle(auction.getTitle());
        dto.setDescription(auction.getDescription());
        dto.setDetailedDescription(auction.getDetailedDescription());
        dto.setStartDateTime(auction.getStartDateTime());
        dto.setEndDateTime(auction.getEndDateTime());
        dto.setStatus(auction.getStatus());
        dto.setIncrementValue(auction.getIncrementValue());
        dto.setMinimumBid(auction.getMinimumBid());
        
        if (auction.getCategory() != null) {
            dto.setCategoryName(auction.getCategory().getName());
            dto.setCategoryId(auction.getCategory().getId());
        }
        
        if (auction.getSeller() != null) {
            dto.setSellerName(auction.getSeller().getName());
            dto.setSellerId(auction.getSeller().getId());
        }
        
        if (auction.getImages() != null) {
            dto.setImageUrls(auction.getImages().stream().map(Image::getUrl).toList());
            if (!dto.getImageUrls().isEmpty()) {
                dto.setCoverImage(dto.getImageUrls().get(0));
            }
        }
        
        return dto;
    }
}