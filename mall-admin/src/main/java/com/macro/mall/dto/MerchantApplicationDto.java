package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class MerchantApplicationDto {
    @ApiModelProperty("Merchant ID / Application ID")
    private Long id;

    @ApiModelProperty("Merchant Name")
    private String name;

    @ApiModelProperty("Contact Person Name")
    private String contactName;

    @ApiModelProperty("Contact Email")
    private String contactEmail;

    @ApiModelProperty("Submission Date (either from mer_merchant_qualification or mer_merchant create_time)")
    private Date submissionDate; 

    @ApiModelProperty("Status: 0->Pending, 1->Approved, 2->Rejected, 3->Active, 4->Inactive")
    private Integer status;

    @ApiModelProperty("Status Description (e.g., 'Pending Review', 'Approved')")
    private String statusDescription;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusDescription() {
        // This logic can be enhanced in the service layer when mapping
        if (status == null) return "Unknown";
        switch (status) {
            case 0: return "Pending";
            case 1: return "Approved (Pending Activation)";
            case 2: return "Rejected";
            case 3: return "Active";
            case 4: return "Inactive";
            default: return "Unknown Status Code: " + status;
        }
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
