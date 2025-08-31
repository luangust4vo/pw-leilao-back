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
    private byte[] profileImage;
    private LocalDateTime createdAt;
    private List<PersonProfileInfo> personProfiles;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PersonProfileInfo {
        private ProfileInfo profile;
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProfileInfo {
            private Long id;
            private String type;
        }
    }
}
