package com.backend.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    String generateHashValue(JsonNode jsonNode){
        if(jsonNode.path("tool").asText().equals("CodeQL"))
            return generateFindingIdCodeQL(jsonNode);

        else if(jsonNode.path("tool").asText().equals("Dependabot"))
            return generateFindingIdDependabot(jsonNode);

        else return generateFindingIdSecretScan(jsonNode);
    }
    private String generateFindingIdCodeQL(JsonNode node){
        //JsonNode mostRecentInstance = node.path("most_recent_instance");
        //JsonNode locationNode = mostRecentInstance.path("location");

        String repoName = "just-another-vulnerable-java-application";
        String pathOfIssue = node.path("pathIssue").asText();
        String startColumn = node.path("startColumn").asText();
        String endColumn = node.path("endColumn").asText();
        String startLine = node.path("startLine").asText();
        String endLine = node.path("endLine").asText();

        String inputString = repoName + pathOfIssue + startColumn + endColumn + startLine + endLine;

        return generateUniqueID(inputString);
    }

    private String generateFindingIdDependabot(JsonNode node){

        String repoName = "just-another-vulnerable-java-application";
        String dependabotSummary;
        String cveId;

        dependabotSummary = node.path("summary").asText();
        cveId = node.path("cve_id").asText();

        String inputString = repoName + dependabotSummary + cveId;
        return generateUniqueID(inputString);
    }

    private String generateFindingIdSecretScan(JsonNode node){

        String repoName = "just-another-vulnerable-java-application";
        String secretType;
        String secret;

        secretType = node.path("secretType").asText();
        secret = node.path("secret").asText();

        String inputString = repoName + secretType + secret;
        return generateUniqueID(inputString);
    }
    private static String generateUniqueID(String inputString){
        try {
            // Create an SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply the hash function to the input string
            byte[] hashBytes = digest.digest(inputString.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Return the full hexadecimal string
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Handle the exception (e.g., print error message or throw RuntimeException)
            e.printStackTrace();
            return null;
        }
    }
}
