package com.github.luangust4vo.pw_leilao_backend.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;

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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(BusinessException ex) {
        ApiResponseDTO<?> errorResponse = ApiResponseDTO.erro(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleNotFoundException(NotFoundException ex) {
        ApiResponseDTO<?> errorResponse = ApiResponseDTO.erro(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponseDTO<?> errorResponse = ApiResponseDTO.erro("Erro de validação", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        ApiResponseDTO<?> errorResponse = ApiResponseDTO.erro("Ocorreu um erro interno inesperado.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}