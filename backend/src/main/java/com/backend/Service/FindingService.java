package com.backend.Service;

import com.backend.Entity.Findings;
//import com.backend.Kafka.KafkaProducerService;
import com.backend.Kafka.KafkaProducerService;
import com.backend.Parser.GithubDataParser;
import com.backend.Repository.FindingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class FindingService {

    @Value("${github.api.url.codeScan}")
    private String githubApiUrlCodeScan;

    @Value("${github.api.url.dependabot}")
    private String githubApiUrlDependabot;

    @Value("${github.api.url.secretScan}")
    private String githubApiUrlSecretScan;

    @Value("${github.access.token}")
    private String githubAccessToken;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private FindingRepository findingRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GithubDataParser githubDataParser;

    @Autowired
    private HashService hashService;

//    public Findings saveFinding(Findings finding) {
//        return findingRepository.save(finding);
//    }

    public Page<Findings> getAllFindings(Pageable pageable, String organizationId){
//        System.out.println("Service se hoon: " + organizationId);
        return findingRepository.findByOrganizationId(organizationId,pageable);
    }

    public Page<Findings> getFindingsForSeverity(String severity,String organizationId, Pageable pageable){
        return findingRepository.findBySeverityAndOrganizationId(severity,organizationId,pageable);
    }

    public Page<Findings> getFindingsForTool(String tool, String organizationId, Pageable pageable){
        return findingRepository.findByToolAndOrganizationId(tool,organizationId,pageable);
    }

    public Page<Findings> getFindingsForSeverityAndTool(String severity, String tool, String organizationId ,Pageable pageable){
        return findingRepository.findBySeverityAndToolAndOrganizationId(severity,tool,organizationId,pageable);
    }


    public void processAndSaveFindings(String organizationId) {

        List<JsonNode> findingsList = new ArrayList<>();

        //Deleting all data from the elasticsearch
        //findingRepository.deleteAll();

        // Fetch and parse data from CodeQL
        List<JsonNode> codeqlFindings = githubDataParser.parseGitHubData(githubApiUrlCodeScan, "CodeQL");

        // Fetch and parse data from Dependabot
        List<JsonNode> dependabotFindings = githubDataParser.parseGitHubData(githubApiUrlDependabot, "Dependabot");

        List<JsonNode> secretScanFindings = githubDataParser.parseGitHubData(githubApiUrlSecretScan, "SecretScan");

        // Merge the findings from both sources
        findingsList.addAll(codeqlFindings);
        findingsList.addAll(dependabotFindings);
        findingsList.addAll(secretScanFindings);

        // Sending the data to Kafka
//        for (JsonNode node : findingsList) {
//            kafkaProducerService.sendMessage(node.toString());
//        }

        //Adding all the data to the elasticsearch
//        saveJsonNodes(findingsList);

        //Create HashTable and store Hash for each issue in ES
        Iterable<Findings> findingsInElasticSearch = findingRepository.findByOrganizationId(organizationId);
        Hashtable<String, Findings> hashtable = new Hashtable<>();

        for (Findings finding : findingsInElasticSearch) {
            String hashValue = hashService.generateHashValue(finding.convertFindingsToJsonNode(finding));
            hashtable.put(hashValue, finding);
//            System.out.println(hashValue);
        }

        //Iterate through findingsList and generate its hash and check if it is present in HashTable or not
        // If yes then check the status has changed or not -> if yes then remove the entry from es and add this new one or else discard
        // If no then save this issue in ES

        for (JsonNode findingFromGithub: findingsList){
            String currHash = hashService.generateHashValue(findingFromGithub);
//            System.out.println(currHash);

//            JsonNode findingForConsumer = findingFromGithub;

//            System.out.println(findingFromGithub.get("status").asText() + " Before conversion for github");

            String stateInFindingFromGithub = findingFromGithub.get("status").asText();

            if(hashtable.containsKey(currHash))
            {
                String stateInELasticSearch = hashtable.get(currHash).getStatus();
                if(!stateInELasticSearch.equals(stateInFindingFromGithub)) //State has changed
                {

                    Long esId = hashtable.get(currHash).getId();
                    // As the state has changed so we will have to update the status of ticket

                    //Mapping the status field as of Armorcode

                    ((ObjectNode) findingFromGithub).put("status", getMappingForStatus(stateInFindingFromGithub));
                    ((ObjectNode) findingFromGithub).put("change", "state change");
                    ((ObjectNode) findingFromGithub).put("id", esId);
                    kafkaProducerService.sendMessage(findingFromGithub);

//                    findingRepository.deleteById(hashtable.get(currHash).getId());
                    saveJsonNode(findingFromGithub, organizationId);
                }
            }

            else if(stateInFindingFromGithub.equals("open"))
            {
                // As a new issue is found so We have to create A Jira Ticket
                ((ObjectNode) findingFromGithub).put("change", "new issue");

                //Mapping the status field as of Armorcode
                ((ObjectNode) findingFromGithub).put("status", getMappingForStatus(findingFromGithub.get("status").asText()));
                kafkaProducerService.sendMessage(findingFromGithub);

                hashtable.put(currHash,convertJsonNodeToFindings(findingFromGithub,organizationId));
                saveJsonNode(findingFromGithub, organizationId);
            }
        }

        // Filtering the queries.
        // Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        //return findingRepository.findByOrganizationId(organizationId);
        }

        public String getMappingForSeverity(String severity){
            return switch (severity) {
                case "critical" -> "critical";
                case "high" -> "high";
                case "medium" -> "medium";
                case "low" -> "low";
                case "warning", "error" -> "info";
                default -> "False Positive";
            };
        }

        public String getMappingForStatus(String status){
            return switch (status) {
                case "fixed", "dismissed", "mitigated", "resolved" -> "mitigated";
                default -> "open";
            };
        }

    public void saveJsonNode(JsonNode jsonNode, String organizationId) {

            Findings finding = new Findings();

            if (jsonNode.has("id")) {
                finding.setId(jsonNode.get("id").asLong());
            }
            if (jsonNode.has("severity")) {
                finding.setSeverity(getMappingForSeverity(jsonNode.get("severity").asText()));
            }
            if (jsonNode.has("status")) {
                finding.setStatus(getMappingForStatus(jsonNode.get("status").asText()));
            }
            if (jsonNode.has("summary")) {
                finding.setSummary(jsonNode.get("summary").asText());
            }
            if (jsonNode.has("tool")) {
                finding.setTool(jsonNode.get("tool").asText());
            }
            if (jsonNode.has("cve_id")) {
                finding.setCve_id(jsonNode.get("cve_id").asText());
            }
            if (jsonNode.has("pathIssue")) {
                finding.setPathIssue(jsonNode.get("pathIssue").asText());
            }
            if (jsonNode.has("startColumn")) {
                finding.setStartColumn(jsonNode.get("startColumn").asText());
            }
            if (jsonNode.has("endColumn")) {
                finding.setEndColumn(jsonNode.get("endColumn").asText());
            }
            if (jsonNode.has("startLine")) {
                finding.setStartLine(jsonNode.get("startLine").asText());
            }
            if (jsonNode.has("endLine")) {
                finding.setEndLine(jsonNode.get("endLine").asText());
            }
            if (jsonNode.has("secretType")) {
                finding.setSecretType(jsonNode.get("secretType").asText());
            }
            if (jsonNode.has("secret")) {
                finding.setSecret(jsonNode.get("secret").asText());
            }
            if (jsonNode.has("createdAt")) {
                finding.setCreatedAt(jsonNode.get("createdAt").asText());
            }
            if (jsonNode.has("updatedAt")) {
                finding.setUpdatedAt(jsonNode.get("updatedAt").asText());
            }

            finding.setOrganizationId(organizationId);

        findingRepository.save(finding);
    }

    public void deleteAllFindings() {
        findingRepository.deleteAll();
    }

    public Findings convertJsonNodeToFindings(JsonNode jsonNode, String organizationId) {

        if(jsonNode == null) return null;

        Findings finding = new Findings();

        if (jsonNode.has("id")) {
            finding.setId(jsonNode.get("id").asLong());
        }
        if (jsonNode.has("severity")) {
            finding.setSeverity(getMappingForSeverity(jsonNode.get("severity").asText()));
        }
        if (jsonNode.has("status")) {
            finding.setStatus(getMappingForStatus(jsonNode.get("status").asText()));
        }
        if (jsonNode.has("summary")) {
            finding.setSummary(jsonNode.get("summary").asText());
        }
        if (jsonNode.has("tool")) {
            finding.setTool(jsonNode.get("tool").asText());
        }
        if (jsonNode.has("cve_id")) {
            finding.setCve_id(jsonNode.get("cve_id").asText());
        }
        if (jsonNode.has("pathIssue")) {
            finding.setPathIssue(jsonNode.get("pathIssue").asText());
        }
        if (jsonNode.has("startColumn")) {
            finding.setStartColumn(jsonNode.get("startColumn").asText());
        }
        if (jsonNode.has("endColumn")) {
            finding.setEndColumn(jsonNode.get("endColumn").asText());
        }
        if (jsonNode.has("startLine")) {
            finding.setStartLine(jsonNode.get("startLine").asText());
        }
        if (jsonNode.has("endLine")) {
            finding.setEndLine(jsonNode.get("endLine").asText());
        }
        if (jsonNode.has("secretType")) {
            finding.setSecretType(jsonNode.get("secretType").asText());
        }
        if (jsonNode.has("secret")) {
            finding.setSecret(jsonNode.get("secret").asText());
        }
        if (jsonNode.has("createdAt")) {
            finding.setCreatedAt(jsonNode.get("createdAt").asText());
        }
        if (jsonNode.has("updatedAt")) {
            finding.setUpdatedAt(jsonNode.get("updatedAt").asText());
        }

        finding.setOrganizationId(organizationId);

        return finding;
    }
}