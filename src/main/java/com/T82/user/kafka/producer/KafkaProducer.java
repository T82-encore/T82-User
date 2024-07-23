package com.T82.user.kafka.producer;


import com.T82.user.kafka.dto.KafkaStatus;
import com.T82.user.kafka.dto.request.KafkaUserDeleteRequest;
import com.T82.user.kafka.dto.request.KafkaUserSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaStatus<KafkaUserSignUpRequest>> kafkaSignUpTemplate;
    private final KafkaTemplate<String, KafkaStatus<KafkaUserDeleteRequest>> kafkaDeleteTemplate;

    public void sendSignUp(KafkaUserSignUpRequest kafkaUserSignUpRequest, String topic) {
        KafkaStatus<KafkaUserSignUpRequest> kafkaStatus = new KafkaStatus<>(kafkaUserSignUpRequest,"signUp");
        kafkaSignUpTemplate.send(topic, kafkaStatus);
    }

    public void sendDelete(KafkaUserDeleteRequest kafkaUserDeleteRequest, String topic) {
        KafkaStatus<KafkaUserDeleteRequest> kafkaStatus = new KafkaStatus<>(kafkaUserDeleteRequest,"delete");
        kafkaDeleteTemplate.send(topic, kafkaStatus);
    }
}
