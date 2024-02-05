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
//
//    @Column(name = "cwe")
//    private Integer cwe;
//
//    @Column(name = "product_name", unique = true)
//    private String productName;

//    @ManyToOne
//    @JoinColumn(name = "scan_id")
//    private Scan scan;

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
//
//    public Integer getCwe() {
//        return cwe;
//    }
//
//    public void setCwe(Integer cwe) {
//        this.cwe = cwe;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }

//    public Scan getScan() {
//        return scan;
//    }

//    public void setScan(Scan scan) {
//        this.scan = scan;
//    }


    // Add any additional fields and methods as needed
}
