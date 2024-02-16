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

        Pageable pageable = PageRequest.of(page, size);

        if(!severity.isEmpty() && !tool.isEmpty()){
            if(severity.equals("All") && tool.equals("All")) return findingService.getAllFindings(pageable);
            else if(severity.equals("All")) return findingService.getFindingsForTool(tool,pageable);
            else if(tool.equals("All")) return findingService.getFindingsForSeverity(severity,pageable);
            else return findingService.getFindingsForSeverityAndTool(severity,tool,pageable);
        }

        else if(!severity.isEmpty()){
            if(severity.equals("All")) return findingService.getAllFindings(pageable);
            else return findingService.getFindingsForSeverity(severity,pageable);
        }

        else if(!tool.isEmpty()){
            if(tool.equals("All")) return findingService.getAllFindings(pageable);
            else return findingService.getFindingsForTool(tool,pageable);
        }
        return findingService.getAllFindings(pageable);
    }

    @GetMapping("/fetch-and-save")
    public Iterable<Findings> fetchAndSaveFindings() {
        return findingService.processAndSaveFindings();
    }

    @DeleteMapping("/delete-all")
    public String deleteAllFindings() {
        findingService.deleteAllFindings();
        return "All findings deleted from Elasticsearch.";
    }
}

