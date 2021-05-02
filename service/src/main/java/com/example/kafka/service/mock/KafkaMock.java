package com.example.kafka.service.mock;

import com.example.types.kafka.KafkaMessageRq;
import com.example.types.kafka.KafkaMessageRs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaMock {

    @Value("${kafka.response.topic}")
    private String responseTopic;

    private final KafkaTemplate<String, KafkaMessageRs> kafkaMockTemplace;

    public KafkaMock(KafkaTemplate<String, KafkaMessageRs> kafkaMockTemplace) {
        this.kafkaMockTemplace = kafkaMockTemplace;
    }

    @KafkaListener(topics = "#{'${kafka.request.topic}'}", containerFactory = "singleFactory", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    private void responseListener(KafkaMessageRq request) throws Exception{
        log.info("Message in request topic: " + request);
        KafkaMessageRs response = new KafkaMessageRs();
        response.setRqUid(request.getRqUid());
        response.setMessageText("get your message, guy");
        kafkaMockTemplace.send(responseTopic, request.getRqUid(), response);
    }
}
