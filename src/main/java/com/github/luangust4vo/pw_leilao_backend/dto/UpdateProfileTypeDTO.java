package com.github.luangust4vo.pw_leilao_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileTypeDTO {
    @NotBlank(message = "O tipo de perfil não pode estar em branco.")
    private String profileType;
}
