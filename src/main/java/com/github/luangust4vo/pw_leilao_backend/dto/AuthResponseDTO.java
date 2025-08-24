package com.github.luangust4vo.pw_leilao_backend.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserInfoDTO user;
    private long expiresIn;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        private Long id;
        private String name;
        private String email;
        private boolean isActive;
        private byte[] profileImage;
        private Date createdAt;
        private List<ProfileInfoDTO> profiles;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProfileInfoDTO {
            private Long id;
            private String type;
        }
    }
}
