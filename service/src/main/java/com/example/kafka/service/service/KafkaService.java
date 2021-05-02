package com.example.kafka.service.service;

import com.example.types.kafka.KafkaMessageRq;
import com.example.types.kafka.KafkaMessageRs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, KafkaMessageRq> kafkaMessageTemplate;

    private Cache<String, CompletableFuture> cache;

    @Value("${kafka.request.topic}")
    private String requestTopic;

    @Value("${kafka.group.id}")
    private String groupId;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaService(KafkaTemplate<String, KafkaMessageRq> kafkaMessageTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.kafkaMessageTemplate = kafkaMessageTemplate;
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
    }

    public KafkaMessageRs send(String message) throws Exception {
        String id = UUID.randomUUID().toString().replaceAll("-","");
        CompletableFuture<KafkaMessageRs> future = new CompletableFuture<>();
        KafkaMessageRq request = new KafkaMessageRq();
        request.setRqUid(id);
        request.setMessageText(message);
        cache.put(id, future);
        kafkaMessageTemplate.send(requestTopic, id, request);
        return future.get(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "#{'${kafka.response.topic}'}", containerFactory = "singleFactory", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    private void responseListener(KafkaMessageRs response) throws Exception{
        log.info("Message in response topic: " + response);
        CompletableFuture<KafkaMessageRs> future = cache.getIfPresent(response.getRqUid());
        cache.getIfPresent(response.getRqUid()).complete(response);
    }
}
