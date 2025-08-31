package com.github.luangust4vo.pw_leilao_backend.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;

@RestControllerAdvice(basePackages = "com.github.luangust4vo.pw_leilao_backend.controllers")
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(ApiResponseDTO.class);
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {
        
        if (body instanceof ApiResponseDTO || body instanceof AuthResponseDTO || body == null) {
            return body;
        }

        return ApiResponseDTO.sucesso("Operação realizada com sucesso!", body);
    }
}