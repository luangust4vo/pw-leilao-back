package com.github.luangust4vo.pw_leilao_backend.dto;

import lombok.Data;

@Data
public class PersonRequestDTO {
    private String name;
    private String email;
    private String password;
}