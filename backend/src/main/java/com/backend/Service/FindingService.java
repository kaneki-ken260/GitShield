package com.backend.Service;

import com.backend.Entity.Findings;
//import com.backend.Kafka.KafkaProducerService;
import com.backend.Parser.GithubDataParser;
import com.backend.Repository.FindingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

//    public Findings saveFinding(Findings finding) {
//        return findingRepository.save(finding);
//    }

    public Iterable<Findings> processAndSaveFindings() {

        List<JsonNode> findingsList = new ArrayList<>();

        //Deleting all data from the elasticsearch
        findingRepository.deleteAll();

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
        saveJsonNodes(findingsList);

        return findingRepository.findAll();
        }

    public void saveJsonNodes(List<JsonNode> jsonNodes) {
        List<Findings> findingsList = new ArrayList<>();

        for (JsonNode jsonNode : jsonNodes) {
            Findings finding = new Findings();

            if (jsonNode.has("findingId")) {
                finding.setId(jsonNode.get("findingId").asLong());
            }
            if (jsonNode.has("severity")) {
                finding.setSecuritySeverityLevel(jsonNode.get("severity").asText());
            }
            if (jsonNode.has("status")) {
                finding.setState(jsonNode.get("status").asText());
            }
            if (jsonNode.has("summary")) {
                finding.setSummary(jsonNode.get("summary").asText());
            }
            if (jsonNode.has("tool")) {
                finding.setTool(jsonNode.get("tool").asText());
            }
            if (jsonNode.has("cve_value")) {
                finding.setCve_value(jsonNode.get("cve_value").asText());
            }
            if (jsonNode.has("created_at")) {
                finding.setCreated_at(jsonNode.get("created_at").asText());
            }
            if (jsonNode.has("updated_at")) {
                finding.setUpdated_at(jsonNode.get("updated_at").asText());
            }

            // Handling so that fixed issues get removed from the elasticsearch
            if(!Objects.equals(finding.getState(), "fixed"))
                findingsList.add(finding);
        }

        findingRepository.saveAll(findingsList);
    }
    public void deleteAllFindings() {
        findingRepository.deleteAll();
    }
}