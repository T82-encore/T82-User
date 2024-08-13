package com.T82.user.kafka.dto.request;

import java.util.UUID;

public record KafkaUserRequest(
        UUID userId,
        String email,
        String name,
        Boolean isArtist,
        String profileUrl
) {
}
