package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

public class ApplicationRejectionParam {

    @NotEmpty
    @ApiModelProperty(value = "Review notes (required for rejection)", required = true)
    private String reviewNotes;

    // Getters and Setters
    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}
