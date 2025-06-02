package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

public class MerchantQualificationParam {
    @NotEmpty
    @ApiModelProperty(value = "Business License Number", required = true)
    private String businessLicenseNo;

    @NotEmpty
    @ApiModelProperty(value = "Business License Image URL", required = true)
    private String businessLicenseImageUrl;

    @NotEmpty
    @ApiModelProperty(value = "Legal Representative ID Front Image URL", required = true)
    private String legalRepresentativeIdFrontUrl;

    @NotEmpty
    @ApiModelProperty(value = "Legal Representative ID Back Image URL", required = true)
    private String legalRepresentativeIdBackUrl;

    // Getters and Setters
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
}
