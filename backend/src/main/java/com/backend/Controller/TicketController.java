package com.backend.Controller;

import com.backend.Entity.Tickets;
import com.backend.Repository.TicketRepository;
import com.backend.Service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Value("${jira.api.baseUrl}")
    private String jiraApiBaseUrl;

    @GetMapping("/fetchTicketsAndSave")
    public List<JsonNode> fetchTicketsAndSave(){
        String jiraApiUrlGetAllTicket = jiraApiBaseUrl + "/2/search?jql=project=HIL";
        return ticketService.getAllTickets(jiraApiUrlGetAllTicket);
    }

    @GetMapping("/fetchTickets")
    public List<Tickets> fecthAllTickets(){
        return ticketRepository.findAll();
    }
}
