package com.backend.Repository;

import com.backend.Entity.Findings;
import com.backend.Entity.Tickets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, String> {

    Page<Tickets> findByOrganizationId(String organizationId, Pageable pageable);
    Tickets findByFindingIdAndOrganizationId(Long findingId, String OrganizationId);

//    Page<Tickets> findAll(Pageable pageable);

    Page<Tickets> findByPriorityAndOrganizationId(String priority, String organizationId, Pageable pageable);

}
