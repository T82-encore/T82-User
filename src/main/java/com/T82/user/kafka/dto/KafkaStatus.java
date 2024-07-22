package com.T82.user.kafka.dto;

public record KafkaStatus<T>(
        T data, String status
) {
}
