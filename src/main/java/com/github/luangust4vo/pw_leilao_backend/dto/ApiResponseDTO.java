package com.github.luangust4vo.pw_leilao_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private boolean erro;
    private String mensagem;
    private T dados;
    
    public ApiResponseDTO(boolean erro, String mensagem) {
        this.erro = erro;
        this.mensagem = mensagem;
        this.dados = null;
    }
    
    public static <T> ApiResponseDTO<T> sucesso(String mensagem) {
        return new ApiResponseDTO<>(false, mensagem);
    }
    
    public static <T> ApiResponseDTO<T> sucesso(String mensagem, T dados) {
        return new ApiResponseDTO<>(false, mensagem, dados);
    }
    
    public static <T> ApiResponseDTO<T> erro(String mensagem) {
        return new ApiResponseDTO<>(true, mensagem);
    }
    
    public static <T> ApiResponseDTO<T> erro(String mensagem, T dados) {
        return new ApiResponseDTO<>(true, mensagem, dados);
    }
}
