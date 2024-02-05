package com.backend.Service;

import com.backend.Entity.Findings;
import com.backend.Kafka.KafkaProducerService;
import com.backend.Parser.GithubDataParser;
import com.backend.Repository.FindingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class FindingService {

    @Value("${github.api.url.codeScan}")
    private String githubApiUrlCodeScan;

    @Value("${github.api.url.dependabot}")
    private String githubApiUrlDependabot;

    @Value("${github.access.token}")
    private String githubAccessToken;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private FindingRepository findingRepository;  // Assuming you have a FindingRepository

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GithubDataParser githubDataParser;

    public Findings saveFinding(Findings finding) {
        return findingRepository.save(finding);
    }

    public List<JsonNode> processAndSaveFindings() {

        List<JsonNode> findingsList = new ArrayList<>();

        // Fetch and parse data from CodeQL
        List<JsonNode> codeqlFindings = githubDataParser.parseGitHubData(githubApiUrlCodeScan);

        // Fetch and parse data from Dependabot
        List<JsonNode> dependabotFindings = githubDataParser.parseGitHubData(githubApiUrlDependabot);

        // Merge the findings from both sources
        findingsList.addAll(codeqlFindings);
        findingsList.addAll(dependabotFindings);

        return findingsList;
        }

    public void deleteAllFindings() {
        findingRepository.deleteAll();
    }
}
