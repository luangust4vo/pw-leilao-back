package com.github.luangust4vo.pw_leilao_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "{validation.category.name.not-blank}")
    @Size(max = 100, message = "{validation.category.name.size}")
    private String name;

    @Column(name = "observation")
    @Size(max = 500, message = "{validation.category.observation.size}")
    private String observation;
}