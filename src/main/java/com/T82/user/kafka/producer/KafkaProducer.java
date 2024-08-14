package com.T82.user.kafka.producer;


import com.T82.user.kafka.dto.KafkaStatus;
import com.T82.user.kafka.dto.request.KafkaAllowRequest;
import com.T82.user.kafka.dto.request.KafkaUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaStatus<KafkaUserRequest>> kafkaSignUpTemplate;
    private final KafkaTemplate<String, KafkaStatus<KafkaUserRequest>> kafkaDeleteTemplate;
    private final KafkaTemplate<String, KafkaStatus<KafkaAllowRequest>> kafkaDeviceTemplate;

    public void sendSignUp(KafkaUserRequest kafkaUserRequest, String topic) {
        KafkaStatus<KafkaUserRequest> kafkaStatus = new KafkaStatus<>(kafkaUserRequest,"signUp");
        kafkaSignUpTemplate.send(topic, kafkaStatus);
    }

    public void sendDelete(KafkaUserRequest kafkaUserRequest, String topic) {
        KafkaStatus<KafkaUserRequest> kafkaStatus = new KafkaStatus<>(kafkaUserRequest,"delete");
        kafkaDeleteTemplate.send(topic, kafkaStatus);
    }

    public void sendDeviceToken(KafkaAllowRequest kafkaAllowRequest, String topic) {
        KafkaStatus<KafkaAllowRequest> kafkaStatus = new KafkaStatus<>(kafkaAllowRequest,"deviceToken");
        kafkaDeviceTemplate.send(topic, kafkaStatus);
    }
}
