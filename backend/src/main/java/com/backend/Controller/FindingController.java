package com.backend.Controller;

import com.backend.Entity.Findings;
import com.backend.Service.FindingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api/findings")
@CrossOrigin(origins = "http://localhost:5173")
public class FindingController {

    @Value("${github.api.url.codeScan}")
    private String githubApiUrlCodeScan;

    @Value("${github.api.url.dependabot}")
    private String githubApiUrlDependabot;

    @Autowired
    private FindingService findingService;

    @GetMapping("/fetch-and-save")
    public Iterable<Findings> fetchAndSaveFindings() {
        return findingService.processAndSaveFindings();
        //return "Processing and saving findings completed!";
    }

    @DeleteMapping("/delete-all")
    public String deleteAllFindings() {
        findingService.deleteAllFindings();
        return "All findings deleted from Elasticsearch.";
    }
}

