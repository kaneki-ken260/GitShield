package com.backend.Kafka;

import com.backend.Entity.Findings;
import com.backend.Service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Hashtable;

@Service
public class KafkaConsumerService {

    @Autowired
    private TicketService ticketService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "findings-topic", groupId = "my-group-id")
    public void consumeMessage(String message) {
        try {

            JsonNode findings = objectMapper.readTree(message);

            Long findingId = findings.get("id").asLong();
            String summary = findings.get("summary").asText();
            String description = findings.get("summary").asText();
            String issueType = "Task";
            String priority = findings.get("severity").asText();
            String statusChange = findings.get("change").asText();
            String findingStatus = findings.get("status").asText();

            HashMap<String, String> workFlowOfJira = new HashMap<>();

            workFlowOfJira.put("To Do","In Progress");
            workFlowOfJira.put("In Progress","Dev Done");
            workFlowOfJira.put("Dev Done","QA Done");
            workFlowOfJira.put("QA Done","Done");
            workFlowOfJira.put("Done","To Do");

            if(statusChange.equals("new issue") && findingStatus.equals("open")){
                ticketService.createJiraTicket(findingId,summary,description,issueType,priority, statusChange, findingStatus);
            }

            else if(statusChange.equals("state change")){
                ticketService.changeStatusOfTicket(findingId,findingStatus, workFlowOfJira);
            }

        } catch (Exception e) {
            // Handle deserialization exception
            e.printStackTrace();
        }

    }
}

