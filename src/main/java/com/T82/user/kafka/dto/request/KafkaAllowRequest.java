package com.T82.user.kafka.dto.request;


import java.util.UUID;

public record KafkaAllowRequest(
        UUID userId,
        String deviceToken
) {
}
