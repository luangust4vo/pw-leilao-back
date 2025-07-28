package com.github.luangust4vo.pw_leilao_backend.dto;

import java.util.Date;

public interface PersonProjection {
    Long getId();
    String getName();
    String getEmail();
    boolean getIsActive();
    byte[] getPerfilImage();
    Date getCreatedAt();
}