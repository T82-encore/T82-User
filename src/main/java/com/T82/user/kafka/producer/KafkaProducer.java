package com.T82.user.kafka.producer;


import com.T82.user.kafka.dto.KafkaStatus;
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

    public void sendSignUp(KafkaUserSignUpRequest kafkaUserSignUpRequest, String topic) {
        KafkaStatus<KafkaUserSignUpRequest> kafkaStatus = new KafkaStatus<>(kafkaUserSignUpRequest,"signup");
        kafkaSignUpTemplate.send(topic, kafkaStatus);
    }
}
