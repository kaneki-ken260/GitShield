package com.backend.Controller;

import com.backend.Aspect.RequiresAuthorization;
import com.backend.Entity.Tickets;
import com.backend.Repository.TicketRepository;
import com.backend.Service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AuthController authController;

    @Value("${jira.api.baseUrl}")
    private String jiraApiBaseUrl;

    @PostMapping("/fetchTicketsAndSave")
    @RequiresAuthorization
    public List<JsonNode> fetchTicketsAndSave(HttpServletRequest request){

//        if(!authController.validateUser(accessToken,organizationId))
//        {
//            System.out.println("Unauthorized user");
//            return null;
//        }

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

        String jiraApiUrlGetAllTicket = jiraApiBaseUrl + "/2/search?jql=project=HIL";
        return ticketService.getAllTickets(jiraApiUrlGetAllTicket, organizationId);
    }

    @PostMapping("/fetchTickets")
    @RequiresAuthorization
    public Page<Tickets> fecthAllTickets(HttpServletRequest request,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false) String priority){

//        if(!authController.validateUser(accessToken,organizationId))
//        {
//            System.out.println("Unauthorized user");
//            return null;
//        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

        if(priority!=null){
            if(priority.isEmpty() || priority.equals("All")) return ticketRepository.findAll(pageable);
            return ticketRepository.findByPriorityAndOrganizationId(priority, organizationId, pageable);
        }

        return ticketRepository.findByOrganizationId(organizationId,pageable);
    }
}
