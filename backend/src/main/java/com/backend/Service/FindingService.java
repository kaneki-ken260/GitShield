package com.backend.Service;

import com.backend.Entity.Findings;
//import com.backend.Kafka.KafkaProducerService;
import com.backend.Parser.GithubDataParser;
import com.backend.Repository.FindingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

//    @Autowired
//    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GithubDataParser githubDataParser;

    @Autowired
    private HashService hashService;

//    public Findings saveFinding(Findings finding) {
//        return findingRepository.save(finding);
//    }

    public Page<Findings> getAllFindings(Pageable pageable){
        return findingRepository.findAll(pageable);
    }

    public Page<Findings> getFindingsForSeverity(String severity, Pageable pageable){
        return findingRepository.findBySeverity(severity,pageable);
    }

    public Page<Findings> getFindingsForTool(String tool, Pageable pageable){
        return findingRepository.findByTool(tool,pageable);
    }

    public Page<Findings> getFindingsForStatus(String status, Pageable pageable){
        return findingRepository.findByTool(status,pageable);
    }

    public Page<Findings> getFindingsForSeverityAndTool(String severity, String tool, Pageable pageable){
        return findingRepository.findBySeverityAndTool(severity,tool,pageable);
    }

    public Page<Findings> getFindingsForSeverityAndStatus(String severity, String status, Pageable pageable){
        return findingRepository.findBySeverityAndStatus(severity,status,pageable);
    }

    public Page<Findings> getFindingsForToolAndStatus(String tool, String status, Pageable pageable){
        return findingRepository.findByToolAndStatus(tool,status,pageable);
    }

    public Page<Findings> getFindingsForSeverityAndToolAndStatus(String severity, String tool, String status, Pageable pageable){
        return findingRepository.findBySeverityAndToolAndStatus(severity,tool,status,pageable);
    }


    public Iterable<Findings> processAndSaveFindings() {

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
        Iterable<Findings> findingsInElasticSearch = findingRepository.findAll();
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
            if(hashtable.containsKey(currHash))
            {
                String stateInELasticSearch = hashtable.get(currHash).getStatus();
                String stateInFindingFromGithub = findingFromGithub.get("status").asText();

                if(!stateInELasticSearch.equals(stateInFindingFromGithub)) //State has changed
                {
                    findingRepository.deleteById(hashtable.get(currHash).getId());
                    saveJsonNode(findingFromGithub);
                }
            }

            else
            {
                saveJsonNode(findingFromGithub);
                hashtable.put(currHash,convertJsonNodeToFindings(findingFromGithub));
            }
        }

        // Filtering the queries.
        return findingRepository.findAll();
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
                case "open" -> "open";
                case "fixed", "dismissed" -> "mitigated";
                default -> "False Positive";
            };
        }

    public void saveJsonNode(JsonNode jsonNode) {

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

        findingRepository.save(finding);
    }

    // Basically to handle the filters from the frontend part
    private boolean findingMatchesCriteria(Findings finding,
                                           String selectedTool,
                                           String selectedSeverity,
                                           String selectedStatus) {
        // Tool filter
        boolean toolMatches = selectedTool == null || selectedTool.isEmpty() || finding.getTool().equalsIgnoreCase(selectedTool);

        // Severity filter
        boolean severityMatches = selectedSeverity == null || selectedSeverity.isEmpty() || finding.getSeverity().equalsIgnoreCase(selectedSeverity);

        // Status filter
        boolean statusMatches = selectedStatus == null || selectedStatus.isEmpty() || finding.getStatus().equalsIgnoreCase(selectedStatus);

        // Return true if all criteria match
        return toolMatches && severityMatches && statusMatches;
    }
    public void deleteAllFindings() {
        findingRepository.deleteAll();
    }

    public Findings convertJsonNodeToFindings(JsonNode jsonNode) {

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

        return finding;
    }
}