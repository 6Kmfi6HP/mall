package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import java.util.Date;

public class MerchantPackageAssignParam {

    @NotNull
    @ApiModelProperty(value = "Merchant Level ID", required = true)
    private Long levelId;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @DecimalMax(value = "1.00", inclusive = true)
    @ApiModelProperty(value = "Commission Rate (e.g., 0.04 for 4%)", required = true)
    private BigDecimal commissionRate;

    @NotNull
    @ApiModelProperty(value = "Package Start Date (YYYY-MM-DDTHH:mm:ss)", required = true)
    private Date startDate;

    @NotNull
    @ApiModelProperty(value = "Package End Date (YYYY-MM-DDTHH:mm:ss)", required = true)
    private Date endDate;

    @NotNull
    @ApiModelProperty(value = "Is this package currently active?", required = true)
    private Boolean isActive;

    // Getters and Setters
    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
