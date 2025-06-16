package com.github.luangust4vo.pw_leilao_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank(message = "{validation.name.not-blank}")
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "{validation.email.not-blank}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    private String password;
}
