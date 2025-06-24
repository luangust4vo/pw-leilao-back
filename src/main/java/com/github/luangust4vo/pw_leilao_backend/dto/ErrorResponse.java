package com.github.luangust4vo.pw_leilao_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private LocalDateTime dateTime;
    private int code;
    private String error;
    private String message;
    private String path;
    private List<String> details;

    public ErrorResponse(int code, String error, String message, String path, List<String> details) {
        this.dateTime = LocalDateTime.now();
        this.code = code;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }
}
