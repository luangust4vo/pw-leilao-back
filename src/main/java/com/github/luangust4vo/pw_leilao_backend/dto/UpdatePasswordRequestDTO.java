package com.github.luangust4vo.pw_leilao_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {
    @NotBlank(message = "O código de verificação é obrigatório.")
    private String code;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String newPassword;
}