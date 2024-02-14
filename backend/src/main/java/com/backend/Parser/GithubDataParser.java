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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        //To convert Java Objects to and from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> simplifiedData = new ArrayList<>();

        for (JsonNode node : originalData) {
            ObjectNode simplifiedNode = objectMapper.createObjectNode();
            simplifiedNode.put("id", generateFindingId());
            simplifiedNode.put("severity", extractSeverity(node,tool));
            simplifiedNode.put("status", extractStatus(node,tool));
            simplifiedNode.put("summary", extractSummary(node,tool));
            simplifiedNode.put("tool", extractTool(node,tool));
            simplifiedNode.put("cve_id", extractCve(node,tool));
            simplifiedNode.put("pathIssue", extractPath(node,tool));
            simplifiedNode.put("startColumn", extractStartColumn(node,tool));
            simplifiedNode.put("endColumn", extractEndColumn(node,tool));
            simplifiedNode.put("startLine", extractStartLine(node,tool));
            simplifiedNode.put("endLine", extractEndLine(node,tool));
//            simplifiedNode.put("ecosystem", extractEcoSystem(node,tool));
            simplifiedNode.put("secretType", extractSecretType(node,tool));
            simplifiedNode.put("secret", extractSecret(node,tool));

            simplifiedData.add(simplifiedNode);
        }

        return simplifiedData;
    }

    private Long generateFindingId() {
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
            return "high";
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

    private String extractCve(JsonNode node, String tool){

//        JsonNode ruleNode = node.get("rule");
        JsonNode securityAdvisoryNode = node.get("security_advisory");

         if(Objects.equals(tool, "Dependabot"))
            return securityAdvisoryNode.path("cve_id").asText();

         return null;
    }

    private String extractPath(JsonNode node, String tool){
        JsonNode mostRecentInstance = node.path("most_recent_instance");
        JsonNode locationNode = mostRecentInstance.path("location");

        if(Objects.equals(tool,"CodeQL"))
        {
            return locationNode.path("path").asText();
        }
        return null;
    }

    private String extractStartColumn(JsonNode node, String tool){
        JsonNode mostRecentInstance = node.path("most_recent_instance");
        JsonNode locationNode = mostRecentInstance.path("location");

        if(Objects.equals(tool,"CodeQL"))
        {
            return locationNode.path("start_column").asText();
        }
        return null;
    }

    private String extractEndColumn(JsonNode node, String tool){

        JsonNode mostRecentInstance = node.path("most_recent_instance");
        JsonNode locationNode = mostRecentInstance.path("location");

        if(Objects.equals(tool,"CodeQL"))
        {
            return locationNode.path("end_column").asText();
        }
        return null;
    }

    private String extractStartLine(JsonNode node, String tool){

        JsonNode mostRecentInstance = node.path("most_recent_instance");
        JsonNode locationNode = mostRecentInstance.path("location");

        if(Objects.equals(tool,"CodeQL"))
        {
            return locationNode.path("start_line").asText();
        }
        return null;
    }

    private String extractEndLine(JsonNode node, String tool){

        JsonNode mostRecentInstance = node.path("most_recent_instance");
        JsonNode locationNode = mostRecentInstance.path("location");

        if(Objects.equals(tool,"CodeQL"))
        {
            return locationNode.path("end_line").asText();
        }
        return null;
    }

    private String extractSecretType(JsonNode node, String tool){

        if(Objects.equals(tool,"SecretScan")){
            return node.path("secret_type").asText();
        }
        return null;
    }

    private String extractSecret(JsonNode node, String tool){

        if(Objects.equals(tool,"SecretScan")){
            return node.path("secret").asText();
        }
        return null;
    }
}

