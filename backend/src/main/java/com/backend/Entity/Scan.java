package com.backend.Entity;

import jakarta.persistence.*;

@Entity
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sub_product")
    private String subProduct;

    @Column(name = "scan_tool")
    private String scanTool;

    @Column(name = "scan_type")
    private Integer scanType;

    @Column(name = "scan_date")
    private String scanDate;

    @Column(name = "total_findings")
    private Integer totalFindings;

    @Column(name = "duplicate_findings")
    private Integer duplicateFindings;

    @Column(name = "resolved_findings")
    private Integer resolvedFindings;

    @Column(name = "new_findings")
    private Integer newFindings;

    // Constructors, getters, setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(String subProduct) {
        this.subProduct = subProduct;
    }

    public String getScanTool() {
        return scanTool;
    }

    public void setScanTool(String scanTool) {
        this.scanTool = scanTool;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public Integer getTotalFindings() {
        return totalFindings;
    }

    public void setTotalFindings(Integer totalFindings) {
        this.totalFindings = totalFindings;
    }

    public Integer getDuplicateFindings() {
        return duplicateFindings;
    }

    public void setDuplicateFindings(Integer duplicateFindings) {
        this.duplicateFindings = duplicateFindings;
    }

    public Integer getResolvedFindings() {
        return resolvedFindings;
    }

    public void setResolvedFindings(Integer resolvedFindings) {
        this.resolvedFindings = resolvedFindings;
    }

    public Integer getNewFindings() {
        return newFindings;
    }

    public void setNewFindings(Integer newFindings) {
        this.newFindings = newFindings;
    }


    // Add any additional fields and methods as needed
}
