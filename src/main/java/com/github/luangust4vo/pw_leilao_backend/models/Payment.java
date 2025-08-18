package com.github.luangust4vo.pw_leilao_backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.luangust4vo.pw_leilao_backend.models.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    @NotNull(message = "Leilão é obrigatório")
    private Auction auction;
    
    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull(message = "Comprador é obrigatório")
    private Person buyer;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status é obrigatório")
    private PaymentStatus status;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }
}
