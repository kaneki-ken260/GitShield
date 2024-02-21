package com.backend.Controller;

import com.backend.Entity.Tickets;
import com.backend.Repository.TicketRepository;
import com.backend.Service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Value("${jira.api.baseUrl}")
    private String jiraApiBaseUrl;

    @GetMapping("/fetchTicketsAndSave")
    public List<JsonNode> fetchTicketsAndSave(){
        String jiraApiUrlGetAllTicket = jiraApiBaseUrl + "/2/search?jql=project=HIL";
        return ticketService.getAllTickets(jiraApiUrlGetAllTicket);
    }

    @GetMapping("/fetchTickets")
    public Page<Tickets> fecthAllTickets(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false) String priority){

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if(priority!=null){
            if(priority.isEmpty() || priority.equals("All")) return ticketRepository.findAll(pageable);
            return ticketRepository.findByPriority(priority, pageable);
        }

        return ticketRepository.findAll(pageable);
    }
}
