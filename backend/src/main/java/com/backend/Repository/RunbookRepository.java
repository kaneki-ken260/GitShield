package com.backend.Repository;

import com.backend.Entity.Runbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunbookRepository extends JpaRepository<Runbook, Long> {
}
