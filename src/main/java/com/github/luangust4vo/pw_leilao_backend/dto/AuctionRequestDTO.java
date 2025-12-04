package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuctionRequestDTO {
    @NotBlank(message = "{validation.auction.title.not-blank}")
    @Size(max = 200, message = "{validation.auction.title.size}")
    private String title;

    @NotBlank(message = "{validation.auction.description.not-blank}")
    @Size(max = 500, message = "{validation.auction.description.size}")
    private String description;

    @Size(max = 2000, message = "{validation.auction.detailed-description.size}")
    private String detailedDescription;

    @NotNull(message = "{validation.auction.start-date-time.not-null}")
    private LocalDateTime startDateTime;

    @NotNull(message = "{validation.auction.end-date-time.not-null}")
    private LocalDateTime endDateTime;

    @NotNull(message = "{validation.auction.increment-value.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.auction.increment-value.min}")
    private BigDecimal incrementValue;

    @NotNull(message = "{validation.auction.minimum-bid.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.auction.minimum-bid.min}")
    private BigDecimal minimumBid;

    @NotNull(message = "{validation.auction.category.not-null}")
    private Long categoryId;

    @Size(max = 1000, message = "{validation.auction.observation.size}")
    private String observation;

    @Size(min = 1, max = 5, message = "O leilão deve ter entre 1 e 5 imagens.")
    private List<String> imageUrls;
}