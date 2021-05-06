package com.example.kafka.service.config;

import com.example.types.kafka.KafkaMessageRs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerMockConfig {
    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean
    public Map<String, Object> producerMockConfigs() {
        Map<String, Object> props = new HashMap<> ();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "mockProducer");
        return props;
    }

    @Bean
    public ProducerFactory<String, KafkaMessageRs> producerMockFactory() {
        return new DefaultKafkaProducerFactory<> (producerMockConfigs());
    }

    @Bean
    public KafkaTemplate<String, KafkaMessageRs> kafkaMockTemplate() {
        KafkaTemplate<String, KafkaMessageRs> template = new KafkaTemplate<>(producerMockFactory());
        template.setMessageConverter(new StringJsonMessageConverter ());
        return template;
    }
}
