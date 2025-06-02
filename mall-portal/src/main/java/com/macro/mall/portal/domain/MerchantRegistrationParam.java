package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MerchantRegistrationParam {
    @NotEmpty
    @ApiModelProperty(value = "Merchant/Store Name", required = true)
    private String name;

    @NotEmpty
    @ApiModelProperty(value = "Contact Person Name", required = true)
    private String contactName;

    @NotEmpty
    @ApiModelProperty(value = "Contact Phone Number", required = true)
    private String contactPhone;

    @NotEmpty
    @Email
    @ApiModelProperty(value = "Contact Email", required = true)
    private String contactEmail;

    @NotEmpty
    @ApiModelProperty(value = "Merchant Address", required = true)
    private String address;

    @NotNull
    @Valid
    @ApiModelProperty(value = "Merchant Qualifications", required = true)
    private MerchantQualificationParam qualifications;

    // Getters and Setters
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

    public MerchantQualificationParam getQualifications() {
        return qualifications;
    }

    public void setQualifications(MerchantQualificationParam qualifications) {
        this.qualifications = qualifications;
    }
}
