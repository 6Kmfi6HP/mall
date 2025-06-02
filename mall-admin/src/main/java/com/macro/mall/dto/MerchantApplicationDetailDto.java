package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class MerchantApplicationDetailDto {

    @ApiModelProperty("Merchant ID")
    private Long id;

    @ApiModelProperty("Merchant Name")
    private String name;

    @ApiModelProperty("Contact Person Name")
    private String contactName;

    @ApiModelProperty("Contact Phone Number")
    private String contactPhone;

    @ApiModelProperty("Contact Email")
    private String contactEmail;

    @ApiModelProperty("Merchant Address")
    private String address;

    @ApiModelProperty("Status: 0->Pending, 1->Approved, 2->Rejected, 3->Active, 4->Inactive")
    private Integer status;
    
    @ApiModelProperty("Status Description")
    private String statusDescription;

    @ApiModelProperty("Merchant Creation Time")
    private Date createTime;

    @ApiModelProperty("Merchant Last Update Time")
    private Date updateTime;

    // Qualification Details
    @ApiModelProperty("Qualification Record ID")
    private Long qualificationId;
    
    @ApiModelProperty("Business License Number")
    private String businessLicenseNo;

    @ApiModelProperty("Business License Image URL")
    private String businessLicenseImageUrl;

    @ApiModelProperty("Legal Representative ID Front Image URL")
    private String legalRepresentativeIdFrontUrl;

    @ApiModelProperty("Legal Representative ID Back Image URL")
    private String legalRepresentativeIdBackUrl;

    @ApiModelProperty("Qualification Submission Date")
    private Date qualificationSubmissionDate;

    @ApiModelProperty("Qualification Review Date")
    private Date qualificationReviewDate;

    @ApiModelProperty("Qualification Review Status: 0->Pending, 1->Approved, 2->Rejected")
    private Integer qualificationReviewStatus;
    
    @ApiModelProperty("Qualification Review Status Description")
    private String qualificationReviewStatusDescription;

    @ApiModelProperty("Qualification Review Notes")
    private String qualificationReviewNotes;
    
    // Current Package Details (Simplified for now)
    @ApiModelProperty("Current Assigned Package Level Name")
    private String currentPackageLevelName;

    @ApiModelProperty("Current Commission Rate")
    private Double currentCommissionRate;

    @ApiModelProperty("Current Package Start Date")
    private Date currentPackageStartDate;

    @ApiModelProperty("Current Package End Date")
    private Date currentPackageEndDate;

    @ApiModelProperty("Current Package Active Status")
    private Boolean currentPackageIsActive;


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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusDescription() {
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(Long qualificationId) {
        this.qualificationId = qualificationId;
    }
    
    public String getBusinessLicenseNo() {
        return businessLicenseNo;
    }

    public void setBusinessLicenseNo(String businessLicenseNo) {
        this.businessLicenseNo = businessLicenseNo;
    }

    public String getBusinessLicenseImageUrl() {
        return businessLicenseImageUrl;
    }

    public void setBusinessLicenseImageUrl(String businessLicenseImageUrl) {
        this.businessLicenseImageUrl = businessLicenseImageUrl;
    }

    public String getLegalRepresentativeIdFrontUrl() {
        return legalRepresentativeIdFrontUrl;
    }

    public void setLegalRepresentativeIdFrontUrl(String legalRepresentativeIdFrontUrl) {
        this.legalRepresentativeIdFrontUrl = legalRepresentativeIdFrontUrl;
    }

    public String getLegalRepresentativeIdBackUrl() {
        return legalRepresentativeIdBackUrl;
    }

    public void setLegalRepresentativeIdBackUrl(String legalRepresentativeIdBackUrl) {
        this.legalRepresentativeIdBackUrl = legalRepresentativeIdBackUrl;
    }

    public Date getQualificationSubmissionDate() {
        return qualificationSubmissionDate;
    }

    public void setQualificationSubmissionDate(Date qualificationSubmissionDate) {
        this.qualificationSubmissionDate = qualificationSubmissionDate;
    }

    public Date getQualificationReviewDate() {
        return qualificationReviewDate;
    }

    public void setQualificationReviewDate(Date qualificationReviewDate) {
        this.qualificationReviewDate = qualificationReviewDate;
    }

    public Integer getQualificationReviewStatus() {
        return qualificationReviewStatus;
    }

    public void setQualificationReviewStatus(Integer qualificationReviewStatus) {
        this.qualificationReviewStatus = qualificationReviewStatus;
    }
    
    public String getQualificationReviewStatusDescription() {
        if (qualificationReviewStatus == null) return "Unknown";
        switch (qualificationReviewStatus) {
            case 0: return "Pending";
            case 1: return "Approved";
            case 2: return "Rejected";
            default: return "Unknown Status Code: " + qualificationReviewStatus;
        }
    }

    public void setQualificationReviewStatusDescription(String qualificationReviewStatusDescription) {
        this.qualificationReviewStatusDescription = qualificationReviewStatusDescription;
    }

    public String getQualificationReviewNotes() {
        return qualificationReviewNotes;
    }

    public void setQualificationReviewNotes(String qualificationReviewNotes) {
        this.qualificationReviewNotes = qualificationReviewNotes;
    }

    public String getCurrentPackageLevelName() {
        return currentPackageLevelName;
    }

    public void setCurrentPackageLevelName(String currentPackageLevelName) {
        this.currentPackageLevelName = currentPackageLevelName;
    }

    public Double getCurrentCommissionRate() {
        return currentCommissionRate;
    }

    public void setCurrentCommissionRate(Double currentCommissionRate) {
        this.currentCommissionRate = currentCommissionRate;
    }

    public Date getCurrentPackageStartDate() {
        return currentPackageStartDate;
    }

    public void setCurrentPackageStartDate(Date currentPackageStartDate) {
        this.currentPackageStartDate = currentPackageStartDate;
    }

    public Date getCurrentPackageEndDate() {
        return currentPackageEndDate;
    }

    public void setCurrentPackageEndDate(Date currentPackageEndDate) {
        this.currentPackageEndDate = currentPackageEndDate;
    }

    public Boolean getCurrentPackageIsActive() {
        return currentPackageIsActive;
    }

    public void setCurrentPackageIsActive(Boolean currentPackageIsActive) {
        this.currentPackageIsActive = currentPackageIsActive;
    }
}
