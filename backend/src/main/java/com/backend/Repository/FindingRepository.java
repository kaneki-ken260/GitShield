package com.backend.Repository;

import com.backend.Entity.Findings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FindingRepository extends ElasticsearchRepository<Findings, Long> {

    Page<Findings> findByTool(String tool, Pageable pageable);

    Page<Findings> findBySeverity(String severity, Pageable pageable);

    Page<Findings> findByStatus(String status, Pageable pageable);

    Page<Findings> findBySeverityAndTool(String severity, String tool, Pageable pageable);

    Page<Findings> findBySeverityAndStatus(String severity, String status, Pageable pageable);

    Page<Findings> findByToolAndStatus(String tool, String status, Pageable pageable);

    Page<Findings> findBySeverityAndToolAndStatus(String severity, String tool,  String status, Pageable pageable);
}
