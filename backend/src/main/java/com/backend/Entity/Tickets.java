package com.backend.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jira_tickets")
public class Tickets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long table_id;

    @Column(name = "findingId")
    private Long findingId;

    @Column(name="ticketId")
    private String id;

    @Column(name = "ticketPriority")
    private String priority;

    @Column(name = "ticketScanType")
    private String scanType;

    @Column(name = "ticketState")
    private String status;

    @Column(name = "ticketCreatedBy")
    private String createdBy;

    @Column(name = "ticketSummary")
    private String summary;

    @Column(name = "ticketDescription")
    private String Description;

    @Column(name = "ticketIssueType")
    private String issueType;

    private LocalDateTime updatedAt;

    public Long getFindingId() {
        return findingId;
    }

    public void setFindingId(Long findingId) {
        this.findingId = findingId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
// Constructors, getters, and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}