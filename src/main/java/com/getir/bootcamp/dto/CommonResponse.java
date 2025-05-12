package com.getir.bootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "CommonResponse{" +
                "isSucceed=" + isSucceed +
                ", data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                ", validationErrors=" + validationErrors +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommonResponse<?> that = (CommonResponse<?>) o;
        return isSucceed == that.isSucceed && Objects.equals(data, that.data) && Objects.equals(errorMessage, that.errorMessage) && Objects.equals(validationErrors, that.validationErrors) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSucceed, data, errorMessage, validationErrors, timestamp);
    }
}
