package com.T82.user.domain.dto.request;

import java.util.UUID;


public record DeviceTokenRequest(
        UUID userId,
        String deviceToken
) {

}
