package com.backend.Repository;

import com.backend.Entity.Findings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FindingRepository extends ElasticsearchRepository<Findings, Long> {
}
