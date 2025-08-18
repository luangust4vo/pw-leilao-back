package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidResponseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    private BidderInfo bidder;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BidderInfo {
        private Long id;
        private String name;
    }
}
