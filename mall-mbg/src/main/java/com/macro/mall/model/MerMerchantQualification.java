package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class MerMerchantQualification implements Serializable {
    private Long id;

    @ApiModelProperty(value = "FK to mer_merchant.id")
    private Long merchantId;

    @ApiModelProperty(value = "Business License Number")
    private String businessLicenseNo;

    @ApiModelProperty(value = "Business License Image URL")
    private String businessLicenseImageUrl;

    @ApiModelProperty(value = "Legal Representative ID Front Image URL")
    private String legalRepresentativeIdFrontUrl;

    @ApiModelProperty(value = "Legal Representative ID Back Image URL")
    private String legalRepresentativeIdBackUrl;

    @ApiModelProperty(value = "Submission Date")
    private Date submissionDate;

    @ApiModelProperty(value = "Review Date")
    private Date reviewDate;

    @ApiModelProperty(value = "Review Status: 0->Pending, 1->Approved, 2->Rejected")
    private Integer reviewStatus;

    @ApiModelProperty(value = "Review Notes")
    private String reviewNotes;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", merchantId=").append(merchantId);
        sb.append(", businessLicenseNo=").append(businessLicenseNo);
        sb.append(", businessLicenseImageUrl=").append(businessLicenseImageUrl);
        sb.append(", legalRepresentativeIdFrontUrl=").append(legalRepresentativeIdFrontUrl);
        sb.append(", legalRepresentativeIdBackUrl=").append(legalRepresentativeIdBackUrl);
        sb.append(", submissionDate=").append(submissionDate);
        sb.append(", reviewDate=").append(reviewDate);
        sb.append(", reviewStatus=").append(reviewStatus);
        sb.append(", reviewNotes=").append(reviewNotes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
