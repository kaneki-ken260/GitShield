package com.backend.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "findings-topic", groupId = "my-group-id")
    public void consumeMessage(String message) {
        // Process the received message and update Elasticsearch or perform any other action
        System.out.println("Received message: " + message);
        // Add your logic to update Elasticsearch or other actions
    }
}

