package com.github.luangust4vo.pw_leilao_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean erro;
    private String mensagem;
    private T dados;
    
    public ApiResponse(boolean erro, String mensagem) {
        this.erro = erro;
        this.mensagem = mensagem;
        this.dados = null;
    }
    
    public static <T> ApiResponse<T> sucesso(String mensagem) {
        return new ApiResponse<>(false, mensagem);
    }
    
    public static <T> ApiResponse<T> sucesso(String mensagem, T dados) {
        return new ApiResponse<>(false, mensagem, dados);
    }
    
    public static <T> ApiResponse<T> erro(String mensagem) {
        return new ApiResponse<>(true, mensagem);
    }
    
    public static <T> ApiResponse<T> erro(String mensagem, T dados) {
        return new ApiResponse<>(true, mensagem, dados);
    }
}
