package com.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(indexName = "findings")
public class Findings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "security_severity_level")
    private String security_severity_Level;

    @Column(name = "state")
    private String state;

    @Column(name="tool")
    private String tool;

    @Column(name = "summary")
    private String summary;

    @Column(name = "cve_value")
    private String cve_value;

    @Column(name = "created_at")
    private String created_at;

    @Column(name = "updated_at")
    private String updated_at;


    // Constructors, getters, setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecuritySeverityLevel() {
        return security_severity_Level;
    }

    public void setSecuritySeverityLevel(String securitySeverityLevel) {
        this.security_severity_Level = securitySeverityLevel;
    }

    public String getState() {
        return state;
    }

    public void setState(String status) {
        this.state = status;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCve_score() {
        return cve_value;
    }

    public void setCve_value(String cve_value) {
        this.cve_value = cve_value;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
