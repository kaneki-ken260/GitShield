package com.backend.Repository;

import com.backend.Entity.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, String> {

    Tickets findByFindingId(Long findingId);

}
