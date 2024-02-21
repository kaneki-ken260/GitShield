package com.backend.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC_NAME = "findings-topic"; // Change to your topic name

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(JsonNode finding) {
        try {
            // Convert JsonNode to String
            String findingString = new ObjectMapper().writeValueAsString(finding);
            // Send the message
            kafkaTemplate.send(TOPIC_NAME, findingString);
        } catch (Exception e) {
            // Handle serialization exception
            e.printStackTrace();
        }
    }
}

