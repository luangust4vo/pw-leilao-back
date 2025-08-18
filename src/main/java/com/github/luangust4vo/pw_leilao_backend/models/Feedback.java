package com.github.luangust4vo.pw_leilao_backend.models;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    @NotNull(message = "Avaliador é obrigatório")
    private Person evaluator;
    
    @ManyToOne
    @JoinColumn(name = "evaluated_id", nullable = false)
    @NotNull(message = "Pessoa avaliada é obrigatória")
    private Person evaluated;
    
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    @NotNull(message = "Leilão é obrigatório")
    private Auction auction;
    
    @Column(name = "rating", nullable = false)
    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private Integer rating;
    
    @Column(name = "comment", length = 1000)
    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    private String comment;
    
    @Column(name = "feedback_date", nullable = false)
    private LocalDateTime feedbackDate;
    
    @PrePersist
    protected void onCreate() {
        this.feedbackDate = LocalDateTime.now();
    }
}
