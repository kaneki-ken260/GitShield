package com.backend.Parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JiraDataParser {

    @Value("${jira.Access.Token}")
    private String jiraAccessToken;

    @Value(("${jira.Username}"))
    private String jiraUsername;

    public List<JsonNode> parseJiraData(String jiraApiUrl, String organizationId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jiraUsername, jiraAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                jiraApiUrl, HttpMethod.GET, entity, JsonNode.class
        );

        if (responseEntity.getBody() != null) {
            return simplifyJiraData(responseEntity.getBody(), organizationId);
        }

        return null;
    }

    private List<JsonNode> simplifyJiraData(JsonNode originalData, String organizationId) {
        //To convert Java Objects to and from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> simplifiedData = new ArrayList<>();

        JsonNode issueNode = originalData.get("issues");

        for (JsonNode node : issueNode) {
            ObjectNode simplifiedNode = objectMapper.createObjectNode();
            simplifiedNode.put("id", extractId(node));
            simplifiedNode.put("priority", extractPriority(node));
            simplifiedNode.put("scanType", assignScanType());
            simplifiedNode.put("status", extractStatus(node));
            simplifiedNode.put("createdBy", extractCreatedBy(node));
            simplifiedNode.put("summary", extractSummary(node));
            simplifiedNode.put("description", extractDescription(node));
            simplifiedNode.put("issueType", extractIssueType(node));
            simplifiedNode.put("organizationId", organizationId);

            simplifiedData.add(simplifiedNode);
        }

        return simplifiedData;
    }

    private String extractId(JsonNode node){
        return node.path("id").asText();
    }

    private String extractPriority(JsonNode node){
        JsonNode priority = node.path("fields").path("priority");
        return priority.path("name").asText();
    }

    private String assignScanType(){
        return "SAST";
    }

    private String extractStatus(JsonNode node){

        JsonNode status = node.path("fields").path("status");
        return status.path("name").asText();
    }

    private String extractCreatedBy(JsonNode node){

        JsonNode creator = node.path("fields").path("creator");
        return creator.path("displayName").asText();
    }

    private String extractSummary(JsonNode node){

        JsonNode fields = node.path("fields");
        return fields.path("summary").asText();
    }

    private String extractDescription(JsonNode node){
        return node.path("fields").path("description").asText();
    }

    private String extractIssueType(JsonNode node){
        return "Task";
    }
}
