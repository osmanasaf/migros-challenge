package com.example.migros.service;

import com.example.migros.model.CourierLocationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.migros.constants.KafkaConstants.TOPIC_NAME;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, CourierLocationLog> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, CourierLocationLog> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(CourierLocationLog courierLocationLog) {
        kafkaTemplate.send(TOPIC_NAME, courierLocationLog);
    }
}