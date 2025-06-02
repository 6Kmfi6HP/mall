package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class ApplicationApprovalParam {

    @ApiModelProperty(value = "Review notes (optional for approval)")
    private String reviewNotes;

    @NotNull
    @ApiModelProperty(value = "Selected Merchant Level ID to assign upon approval", required = true)
    private Long selectedPackageLevelId;

    // Getters and Setters
    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public Long getSelectedPackageLevelId() {
        return selectedPackageLevelId;
    }

    public void setSelectedPackageLevelId(Long selectedPackageLevelId) {
        this.selectedPackageLevelId = selectedPackageLevelId;
    }
}
