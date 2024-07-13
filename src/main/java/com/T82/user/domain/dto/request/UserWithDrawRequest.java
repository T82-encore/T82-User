package com.T82.user.domain.dto.request;

import com.T82.user.domain.entity.User;

import java.time.LocalDate;
import java.util.UUID;

//추후 토큰 관련된것으로 변경 필요
public record UserWithDrawRequest(
        String email
) {
}
