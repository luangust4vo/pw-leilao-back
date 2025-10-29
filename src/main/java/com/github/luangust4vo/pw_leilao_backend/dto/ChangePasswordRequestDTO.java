package com.github.luangust4vo.pw_leilao_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    @NotBlank(message = "A senha atual é obrigatória.")
    String currentPassword;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres.")
    String newPassword;
}
