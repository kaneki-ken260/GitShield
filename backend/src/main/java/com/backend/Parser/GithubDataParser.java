package com.backend.Parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GithubDataParser {

    @Value("${github.access.token}")
    private String githubAccessToken;

    public List<JsonNode> parseGitHubData(String githubApiUrl, String tool) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                githubApiUrl, HttpMethod.GET, entity, JsonNode.class
        );

        if (responseEntity.getBody() != null) {
            return simplifyGitHubData(responseEntity.getBody(), tool);
        }

        return null;
    }

    private List<JsonNode> simplifyGitHubData(JsonNode originalData, String tool) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> simplifiedData = new ArrayList<>();

        for (JsonNode node : originalData) {
            ObjectNode simplifiedNode = objectMapper.createObjectNode();
            simplifiedNode.put("findingId", generateFindingId());
            simplifiedNode.put("severity", extractSeverity(node,tool));
            simplifiedNode.put("status", extractStatus(node,tool));
            simplifiedNode.put("summary", extractSummary(node,tool));
            simplifiedNode.put("tool", extractTool(node,tool));

            simplifiedData.add(simplifiedNode);
        }

        return simplifiedData;
    }

    private Long generateFindingId() {
        // Implement logic to generate a unique finding ID

        Random random = new Random();
        return (long) (100000 + random.nextInt(900000)); // Ensuring 6 digits
    }

    private String extractSeverity(JsonNode node, String tool) {

        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");
        //JsonNode toolNode = node.get("tool");

        if(Objects.equals(tool, "CodeQL"))
        return ruleNode.path("security_severity_level").asText();

        else if(Objects.equals(tool, "Dependabot"))
            return securityAdvisoryNode.path("severity").asText();

        else
            return null;
    }

    private String extractStatus(JsonNode node, String tool) {
        // Implement logic to extract status based on the GitHub API response structure
        return node.path("state").asText();
    }

    private String extractSummary(JsonNode node, String tool) {
        // Implement logic to extract summary based on the GitHub API response structure

        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");
        //JsonNode toolNode = node.get("tool");

        if(Objects.equals(tool, "CodeQL"))
        return ruleNode.path("description").asText();

        else if(Objects.equals(tool, "Dependabot"))
            return securityAdvisoryNode.path("summary").asText();

        else
            return node.path("secret_type_display_name").asText();
    }

    private String extractTool(JsonNode node, String tool) {
        return tool;
    }
}

