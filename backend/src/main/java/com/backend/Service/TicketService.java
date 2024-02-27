package com.backend.Service;

import com.backend.Entity.Findings;
import com.backend.Entity.Tickets;
import com.backend.Parser.JiraDataParser;
import com.backend.Repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
public class TicketService {

    @Value("${jira.api.baseUrl}")
    private String jiraApiBaseUrl;

    @Value(("${jira.Access.Token}"))
    private String jiraAccessToken;

    @Value(("${jira.Username}"))
    private String jiraUsername;

    @Value(("${jira.Project.Key}"))
    private String jiraProjectKey;

    @Autowired
    private JiraDataParser jiraDataParser;

    @Autowired
    private TicketRepository ticketRepository;

    public List<JsonNode> getAllTickets(String jiraApiUrl, String organizationId){
        return jiraDataParser.parseJiraData(jiraApiUrl, organizationId);
    }

    public String getMappingForPriority(String priority){
        return switch (priority) {
            case "critical" -> "Highest";
            case "high" -> "High";
            case "medium" -> "Medium";
            case "low" -> "Low";
            case "warning", "error","info" -> "Lowest";
            default -> "Medium";
        };
    }

    public void createJiraTicket(Long findingId, String summary, String description, String issueType, String priority, String statusChange, String findingStatus, String organizationId) {
        try {
            // Use Jackson ObjectMapper to build JSON payload
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.createObjectNode();
            ObjectNode fieldsNode = jsonNode.putObject("fields");

            fieldsNode.putObject("project")
                    .put("key", jiraProjectKey);
            fieldsNode.put("summary", summary);
            fieldsNode.put("description", description);
            fieldsNode.putObject("issuetype")
                    .put("name", issueType);
            fieldsNode.putObject("priority")
                    .put("name", getMappingForPriority(priority));
//            fieldsNode.putObject("status")
//                    .put("name", "To Do");

            // Convert JSON to String
            String requestBody = jsonNode.toString();

            // Print request body for debugging
//            System.out.println("Request Body: " + requestBody);

            // Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(jiraUsername, jiraAccessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Set up HTTP entity with headers and body
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make API call
            String apiUrl = jiraApiBaseUrl + "/2/issue";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class);

//            System.out.println(response);

            // Check response status
            if (response.getStatusCode().is2xxSuccessful()) {
//                System.out.println("Jira ticket created successfully!");
                JsonNode responseBody = getTicketUsingId(Objects.requireNonNull(response.getBody()).path("id").asText());
                ((ObjectNode) responseBody).put("findingId", findingId);

                saveJsonNode(responseBody, organizationId);

            } else {
                System.err.println("Failed to create Jira ticket. Response: " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error communicating with Jira API. Response: " + e.getResponseBodyAsString());
            System.out.println(e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public JsonNode getTicketUsingId(String id){

        RestTemplate restTemplate = new RestTemplate();

        String jiraApiUrl = jiraApiBaseUrl + "/2/issue/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jiraUsername, jiraAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                jiraApiUrl, HttpMethod.GET, entity, JsonNode.class
        );

        if (responseEntity.getBody() != null) {
            return simplifyJiraTicket(responseEntity.getBody());
        }

        return null;
    }

    public JsonNode simplifyJiraTicket(JsonNode node){

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode simplifiedNode = objectMapper.createObjectNode();
        simplifiedNode.put("id", extractId(node));
        simplifiedNode.put("priority", extractPriority(node));
        simplifiedNode.put("scanType", assignScanType());
        simplifiedNode.put("status", extractStatus(node));
        simplifiedNode.put("createdBy", extractCreatedBy(node));
        simplifiedNode.put("summary", extractSummary(node));
        simplifiedNode.put("description", extractDescription(node));
        simplifiedNode.put("issueType", extractIssueType(node));
        simplifiedNode.put("findingId", extractFindingId(node));

        return simplifiedNode;
    }

    private long extractFindingId(JsonNode node) {
        return node.path("findingId").asLong();
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

    private void saveJsonNode(JsonNode jsonNode, String organizationId){
        Tickets ticket = new Tickets();

        if (jsonNode.has("id")) {
            ticket.setId(jsonNode.get("id").asText());
        }
        if (jsonNode.has("priority")) {
            ticket.setPriority(jsonNode.get("priority").asText());
        }
        if (jsonNode.has("scanType")) {
            ticket.setScanType(jsonNode.get("scanType").asText());
        }
        if (jsonNode.has("status")) {
            ticket.setStatus(jsonNode.get("status").asText());
        }
        if (jsonNode.has("createdBy")) {
            ticket.setCreatedBy(jsonNode.get("createdBy").asText());
        }
        if (jsonNode.has("summary")) {
            ticket.setSummary(jsonNode.get("summary").asText());
        }
        if (jsonNode.has("description")) {
            ticket.setDescription(jsonNode.get("description").asText());
        }
        if (jsonNode.has("issueType")) {
            ticket.setIssueType(jsonNode.get("issueType").asText());
        }
        if (jsonNode.has("findingId")) {
            ticket.setFindingId(jsonNode.get("findingId").asLong());
        }

        ticket.setOrganizationId(organizationId);

        long milliseconds = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(milliseconds);
        ticket.setUpdatedAt(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());

        ticketRepository.save(ticket);
    }

    public void changeStatusOfTicket(Long findingId, String findingStatus, HashMap<String, String> workFlowOfJira, String organizationId){

        Tickets ticket = ticketRepository.findByFindingIdAndOrganizationId(findingId, organizationId);
        String ticketId = "";

        long milliseconds = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(milliseconds);

        if(ticket==null) return;

        ticketId = ticket.getId();
        if(findingStatus.equals("mitigated")){
              ticket.setStatus("Done");
              ticket.setUpdatedAt(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
              ticketRepository.save(ticket);
              updateJiraTicketStatus(ticketId,"Done", workFlowOfJira);
        }

        else{
            ticket.setStatus("To Do");
            ticket.setUpdatedAt(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
            ticketRepository.save(ticket);
            updateJiraTicketStatus(ticketId,"To Do", workFlowOfJira);
        }
    }

    public void updateJiraTicketStatus(String issueKey, String transitionName, HashMap<String, String> workFlowOfJira) {
        try {
            JsonNode currIssue = getTicketUsingId(issueKey);
            System.out.println("Curr Issue" + currIssue);
            System.out.println("Issue Key" + issueKey);
            String currIssueStatus = currIssue.get("status").asText();

            while (!currIssueStatus.equals(transitionName)){

//                System.out.println("Curr Issue Status" + currIssueStatus);
                int transitionId = getTransitionId(issueKey, workFlowOfJira.get(currIssueStatus));
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(jiraUsername, jiraAccessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                String requestBody = "{ \"transition\": { \"id\": " + transitionId + " } }";
                System.out.println(requestBody);
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                String apiUrl = jiraApiBaseUrl + "/2/issue/" + issueKey + "/transitions";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class);

                //System.out.println(response);
                if (response.getStatusCode().is2xxSuccessful()) {
                    currIssue = getTicketUsingId(issueKey);
                    currIssueStatus = currIssue.path("status").asText();
                    System.out.println("Jira ticket status updated successfully!");
                } else {
                    System.err.println("Failed to update Jira ticket status. Response: " + response.getBody());
                }
            }

        } catch (HttpClientErrorException e) {
            System.err.println("Error communicating with Jira API. Response: " + e.getResponseBodyAsString());
            System.out.println(e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private int getTransitionId(String issueKey, String transitionName) {
        System.out.println(transitionName);
        String apiUrl = jiraApiBaseUrl + "/2/issue/" + issueKey + "/transitions";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jiraUsername, jiraAccessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, JsonNode.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode transitions = response.getBody().get("transitions");
            for (JsonNode transition : transitions) {
                String name = transition.get("to").get("name").asText();
//                System.out.println(name+"   --  "+transitionName);
                if (name.equalsIgnoreCase(transitionName)) {
                    return transition.get("id").asInt();
                }
            }
            throw new RuntimeException("Transition not found: " + transitionName);
        } else {
            throw new RuntimeException("Failed to retrieve transitions. Response: " + response.getBody());
        }
    }
}
