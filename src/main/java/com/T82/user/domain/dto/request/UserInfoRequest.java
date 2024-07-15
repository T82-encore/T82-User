package com.T82.user.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//    추후 토큰 형식에 맞춰 DTO 변경 필요
public record UserInfoRequest(
        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {
}
