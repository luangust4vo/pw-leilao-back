package com.github.luangust4vo.pw_leilao_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "url", nullable = false, length = 500)
    @NotBlank(message = "URL da imagem é obrigatória")
    @Size(max = 500, message = "URL deve ter no máximo 500 caracteres")
    private String url;
    
    @Column(name = "description", length = 200)
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
}
