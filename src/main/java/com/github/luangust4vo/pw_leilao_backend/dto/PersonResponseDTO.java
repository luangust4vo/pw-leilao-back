package com.github.luangust4vo.pw_leilao_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {
    private Long id;
    private String name;
    private String email;
    private boolean isActive;
    private String profileImage;
    private LocalDateTime createdAt;
    private List<ProfileInfoDTO> profiles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfoDTO {
        private Long id;
        private String type;
    }
}
