package com.backend.Repository;

import com.backend.Entity.Findings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FindingRepository extends ElasticsearchRepository<Findings, Long> {

    Page<Findings> findByOrganizationId(String organizationId, Pageable pageable);
    Iterable<Findings> findByOrganizationId(String organizationId);
//    Iterable<Findings> findAll(String organizationId);
    Page<Findings> findByToolAndOrganizationId(String tool, String organizationId, Pageable pageable);
    Page<Findings> findBySeverityAndOrganizationId(String severity, String organizationId, Pageable pageable);
    Page<Findings> findBySeverityAndToolAndOrganizationId(String severity, String tool, String organizationId, Pageable pageable);
}
