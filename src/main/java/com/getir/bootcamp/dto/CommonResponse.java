package com.getir.bootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean isSucceed;
    private T data;
    private String errorMessage;
    private Map<String, String> validationErrors;
    private LocalDateTime timestamp;

    public static <T> CommonResponse<T> ok(T data) {
        return CommonResponse.<T>builder()
                .isSucceed(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> CommonResponse<T> error(String errorMessage) {
        return CommonResponse.<T>builder()
                .isSucceed(false)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> CommonResponse<T> validationError(Map<String, String> validationErrors) {
        return CommonResponse.<T>builder()
                .isSucceed(false)
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> CommonResponse<T> noContent() {
        return CommonResponse.<T>builder()
                .isSucceed(true)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
