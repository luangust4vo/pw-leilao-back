package com.github.luangust4vo.pw_leilao_backend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
public class AvatarRequestDTO {
    @NotNull
    private Long userId;
    private MultipartFile file;
}
