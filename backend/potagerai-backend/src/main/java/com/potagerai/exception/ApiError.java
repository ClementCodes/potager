package com.potagerai.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp,
    List<String> details
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, LocalDateTime.now(), List.of());
    }

    public static ApiError of(int status, String error, String message, String path, List<String> details) {
        return new ApiError(status, error, message, path, LocalDateTime.now(), details);
    }
}
