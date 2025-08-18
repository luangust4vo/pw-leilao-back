package com.github.luangust4vo.pw_leilao_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BidRequestDTO {
    @NotNull(message = "{validation.bid.amount.not-null}")
    @DecimalMin(value = "0.01", message = "{validation.bid.amount.min}")
    private BigDecimal amount;
}
