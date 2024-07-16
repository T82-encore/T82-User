package com.T82.user.domain.dto.response;

public record TokenResponse(
        String token,
        String tokenType
) {
    public static TokenResponse from(String token) {
        return new TokenResponse(token, "Bearer");
    }
}
