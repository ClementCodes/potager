package com.potagerai.dto.auth;

public record AuthResponse(

    String token,
    String tokenType,
    Long userId,
    String email
) {
    public static AuthResponse of(String token, Long userId, String email) {
        return new AuthResponse(token, "Bearer", userId, email);
    }
}
