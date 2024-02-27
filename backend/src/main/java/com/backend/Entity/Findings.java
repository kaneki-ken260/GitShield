package com.backend.Entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(indexName = "afindings")
public class Findings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    private String severity;

    @Field
    private String status;

    @Field
    private String tool;

    @Field
    private String summary;

    @Field
    private String cve_id;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Field
    private String createdAt;

    @Field
    private String updatedAt;

    @Field
    private String pathIssue;
    @Field
    private String startColumn;

    @Field
    private String endColumn;

    @Field
    private String startLine;

    @Field
    private String endLine;

    @Field
    private String secretType;

    @Field
    private String secret;

    @Field
    private String organizationId;

    @Field
    private String issueNumber;

    // Constructors, getters, setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String securitySeverityLevel) {
        this.severity = securitySeverityLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCve_id() {
        return cve_id;
    }

    public void setCve_id(String cve_id) {
        this.cve_id = cve_id;
    }

    public String getSecurity_severity_Level() {
        return severity;
    }

    public void setSecurity_severity_Level(String severity) {
        this.severity = severity;
    }

    public String getPathIssue() {
        return pathIssue;
    }

    public void setPathIssue(String pathIssue) {
        this.pathIssue = pathIssue;
    }

    public String getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(String startColumn) {
        this.startColumn = startColumn;
    }

    public String getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(String endColumn) {
        this.endColumn = endColumn;
    }

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public String getSecretType() {
        return secretType;
    }

    public void setSecretType(String secretType) {
        this.secretType = secretType;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getEndLine() {
        return endLine;
    }

    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public JsonNode convertFindingsToJsonNode(Findings finding) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(finding, JsonNode.class);
    }
}
