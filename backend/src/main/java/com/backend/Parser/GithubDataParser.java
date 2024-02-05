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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Component
public class GithubDataParser {

    @Value("${github.access.token}")
    private String githubAccessToken;

    public List<JsonNode> parseGitHubData(String githubApiUrl) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                githubApiUrl, HttpMethod.GET, entity, JsonNode.class
        );

        if (responseEntity.getBody() != null) {
            return simplifyGitHubData(responseEntity.getBody());
        }

        return null;
    }

    private List<JsonNode> simplifyGitHubData(JsonNode originalData) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> simplifiedData = new ArrayList<>();

        for (JsonNode node : originalData) {
            ObjectNode simplifiedNode = objectMapper.createObjectNode();
            simplifiedNode.put("findingId", generateFindingId());
            simplifiedNode.put("severity", extractSeverity(node));
            simplifiedNode.put("status", extractStatus(node));
            simplifiedNode.put("summary", extractSummary(node));
            simplifiedNode.put("tool", extractTool(node));

            simplifiedData.add(simplifiedNode);
        }

        return simplifiedData;
    }

    private Long generateFindingId() {
        // Implement logic to generate a unique finding ID

        Random random = new Random();
        return (long) (100000 + random.nextInt(900000)); // Ensuring 6 digits
    }

    private String extractSeverity(JsonNode node) {
        // Implement logic to extract severity based on the GitHub API response structure

        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");
        JsonNode toolNode = node.get("tool");

        if(toolNode!=null)
        return ruleNode.path("security_severity_level").asText();

        else
            return securityAdvisoryNode.path("severity").asText();
    }

    private String extractStatus(JsonNode node) {
        // Implement logic to extract status based on the GitHub API response structure
        return node.path("state").asText();
    }

    private String extractSummary(JsonNode node) {
        // Implement logic to extract summary based on the GitHub API response structure

        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");
        JsonNode toolNode = node.get("tool");

        if(toolNode!=null)
        return ruleNode.path("description").asText();

        else {
            return securityAdvisoryNode.path("summary").asText();
        }
    }

    private String extractTool(JsonNode node) {
        // Implement logic to extract tool based on the GitHub API response structure
        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");
        JsonNode toolNode = node.get("tool");

        if(toolNode!=null){
            return toolNode.path("name").asText();
        }
        else {
            return "Dependabot";
        }
    }
}

