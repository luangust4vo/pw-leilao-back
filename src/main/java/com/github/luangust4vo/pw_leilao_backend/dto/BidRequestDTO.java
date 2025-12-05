package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BidRequestDTO {
    @NotNull(message = "O leilão é obrigatório")
    private Long auctionId;

    @NotNull(message = "O valor do lance é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal amount;
}