package com.backend.Controller;

import com.backend.Entity.Findings;
import com.backend.Repository.FindingRepository;
import com.backend.Service.FindingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class FindingController {

    @Value("${github.api.url.codeScan}")
    private String githubApiUrlCodeScan;

    @Value("${github.api.url.dependabot}")
    private String githubApiUrlDependabot;

    @Autowired
    private FindingService findingService;

    @GetMapping("/fetchFindings")
    public Page<Findings> fetchFindings(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String severity,
                                        @RequestParam(required = false) String tool){

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (severity != null && tool != null) {
            if (severity.isEmpty() && tool.isEmpty()) {
                return findingService.getAllFindings(pageable);
            } else if (severity.isEmpty()) {
                return findingService.getFindingsForTool(tool, pageable);
            } else if (tool.isEmpty()) {
                return findingService.getFindingsForSeverity(severity, pageable);
            } else {
                return findingService.getFindingsForSeverityAndTool(severity, tool, pageable);
            }
        } else if (severity != null && severity.isEmpty()) {
            return findingService.getAllFindings(pageable);
        } else if (tool != null && tool.isEmpty()) {
            return findingService.getAllFindings(pageable);
        }
        return findingService.getAllFindings(pageable);
    }

    @GetMapping("/fetch-and-save")
    public Iterable<Findings> fetchAndSaveFindings() {
        return findingService.processAndSaveFindings();
    }

    @GetMapping("/allFindings")
    public Page<Findings> getTotalFindings(@RequestParam(defaultValue = "0", required = false) int currentPage,
                                            @RequestParam(defaultValue = "200", required = false) int pageSize) {

        currentPage = Math.max(currentPage, 0);


        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize,Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getAllFindings(pageable);

    }

    @GetMapping("/codeQLFindings")
    public Page<Findings> getCodeQLFindings(@RequestParam(defaultValue = "0", required = false) int currentPage,
                                            @RequestParam(defaultValue = "200", required = false) int pageSize) {
        // Ensure that page number is not less than 0
        String tool = "codeQL";
        currentPage = Math.max(currentPage, 0);


        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getFindingsForTool(tool, pageable);

    }

    @GetMapping("/dependabotFindings")
    public Page<Findings> getDependabotFindings(@RequestParam(defaultValue = "0", required = false) int currentPage,
                                                @RequestParam(defaultValue = "200", required = false) int pageSize) {
        // Ensure that page number is not less than 0
        String tool = "dependabot";
        currentPage = Math.max(currentPage, 0);


        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getFindingsForTool(tool, pageable);

    }

    @GetMapping("/secretScanningFindings")
    public Page<Findings> getSecretScanningFindings(@RequestParam(defaultValue = "0", required = false) int currentPage,
                                                    @RequestParam(defaultValue = "200", required = false) int pageSize ) {
        // Ensure that page number is not less than 0
        String tool = "secret scanning";
        currentPage = Math.max(currentPage, 0);


        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getFindingsForTool(tool, pageable);

    }

    @DeleteMapping("/delete-all")
    public String deleteAllFindings() {
        findingService.deleteAllFindings();
        return "All findings deleted from Elasticsearch.";
    }
}

