package com.backend.Controller;

//import com.backend.Aspect.RequiresAuthorization;
import com.backend.Aspect.RequiresAuthorization;
import com.backend.Entity.Findings;
import com.backend.Repository.FindingRepository;
import com.backend.Service.FindingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.HttpServletRequest;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class FindingController {

    @Value("${github.api.url.codeScan}")
    private String githubApiUrlCodeScan;

    @Value("${github.api.url.dependabot}")
    private String githubApiUrlDependabot;

    @Autowired
    private FindingService findingService;

    @Autowired
    private AuthController authController;

    @PostMapping("/fetchFindings")
    @RequiresAuthorization
    public Page<Findings> fetchFindings(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String severity,
                                        @RequestParam(required = false) String tool){

//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return null;
//        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

//        System.out.println("Hello: " + accessToken);

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

//        System.out.println("findingcontroller " + organizationId);
//        System.out.println("findingcontroller " + accessToken);
        if (severity != null && tool != null) {
            if (severity.isEmpty() && tool.isEmpty()) {
                return findingService.getAllFindings(pageable, organizationId);
            } else if (severity.isEmpty()) {
                return findingService.getFindingsForTool(tool,organizationId, pageable);
            } else if (tool.isEmpty()) {
                return findingService.getFindingsForSeverity(severity,organizationId, pageable);
            } else {
                return findingService.getFindingsForSeverityAndTool(severity, tool,organizationId, pageable);
            }
        } else if (severity != null && severity.isEmpty()) {
            return findingService.getAllFindings(pageable, organizationId);
        } else if (tool != null && tool.isEmpty()) {
            return findingService.getAllFindings(pageable, organizationId);
        }
        return findingService.getAllFindings(pageable, organizationId);
    }

    @PostMapping("/fetch-and-save")
    @RequiresAuthorization
    public void fetchAndSaveFindings(HttpServletRequest request) {

//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return;
//        }

           String organizationId = request.getHeader("organizationId");
//         System.out.println(accessToken);
         findingService.processAndSaveFindings(organizationId);
    }

    @PostMapping("/allFindings")
    @RequiresAuthorization
    public Page<Findings> getTotalFindings(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0", required = false) int currentPage,
                                           @RequestParam(defaultValue = "200", required = false) int pageSize) {

//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return null;
//        }
        currentPage = Math.max(currentPage, 0);

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");
        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize,Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getAllFindings(pageable,organizationId);

    }

    @PostMapping("/codeQLFindings")
    @RequiresAuthorization
    public Page<Findings> getCodeQLFindings(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0", required = false) int currentPage,
                                            @RequestParam(defaultValue = "200", required = false) int pageSize) {
        // Ensure that page number is not less than 0
//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return null;
//        }
        String tool = "codeQL";
        currentPage = Math.max(currentPage, 0);


        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

        return findingService.getFindingsForTool(tool,organizationId, pageable);

    }

    @PostMapping("/dependabotFindings")
    @RequiresAuthorization
    public Page<Findings> getDependabotFindings(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0", required = false) int currentPage,
                                                @RequestParam(defaultValue = "200", required = false) int pageSize) {
        // Ensure that page number is not less than 0
//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return null;
//        }
        String tool = "dependabot";
        currentPage = Math.max(currentPage, 0);

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getFindingsForTool(tool, organizationId, pageable);

    }

    @PostMapping("/secretScanningFindings")
    @RequiresAuthorization
    public Page<Findings> getSecretScanningFindings(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0", required = false) int currentPage,
                                                    @RequestParam(defaultValue = "200", required = false) int pageSize) {
        // Ensure that page number is not less than 0
//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return null;
//        }
        String tool = "secret scanning";
        currentPage = Math.max(currentPage, 0);

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");
        // Create pageable object with provided page number, size, and sorting (if needed)
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return findingService.getFindingsForTool(tool,organizationId, pageable);

    }

    @PostMapping("/updateState/{issueNumber}")
    @RequiresAuthorization
    public void updateState(
            HttpServletRequest request,
            @PathVariable("issueNumber") String issueNumber,
            @RequestParam("newState") String newState,
            @RequestParam("tool") String tool,
            @RequestParam("dismissal") String dismissal
    ) {

//        if(!authController.validateUser(accessToken,organizationId)) {
//            System.out.println("Unauthorized Access");
//            return;
//        }
        System.out.println("updating" + request.getHeader("organizationId"));
        System.out.println("updating" + request.getHeader("accessToken"));
        findingService.changeFindingStatus(tool, issueNumber, newState, dismissal);

    }

    @DeleteMapping("/delete-all")
    public String deleteAllFindings() {

        findingService.deleteAllFindings();
        return "All findings deleted from Elasticsearch.";
    }
}

