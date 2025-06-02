package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.MerMerchant;
import com.macro.mall.model.MerMerchantLevel;
import com.macro.mall.model.MerMerchantPackage;
import com.macro.mall.service.OmsMerchantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@Api(tags = "OmsMerchantApplicationController", description = "Merchant Application and Management (Admin)")
@RequestMapping("/merchants") // Base path for merchant related operations in admin
public class OmsMerchantApplicationController {

    @Autowired
    private OmsMerchantApplicationService merchantApplicationService;

    @ApiOperation("List Merchant Applications")
    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchant:read')") // Example permission
    public CommonResult<CommonPage<MerchantApplicationDto>> listApplications(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MerchantApplicationDto> applicationList = merchantApplicationService.listApplications(name, status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(applicationList));
    }

    @ApiOperation("Get Merchant Application Detail")
    @RequestMapping(value = "/applications/{merchantId}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchant:read')")
    public CommonResult<MerchantApplicationDetailDto> getApplicationDetail(@PathVariable Long merchantId) {
        MerchantApplicationDetailDto detailDto = merchantApplicationService.getApplicationDetail(merchantId);
        if (detailDto == null) {
            return CommonResult.failed("Application not found");
        }
        return CommonResult.success(detailDto);
    }

    @ApiOperation("Approve Merchant Application")
    @RequestMapping(value = "/applications/{merchantId}/approve", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchant:approve')") // Example permission
    public CommonResult<?> approveApplication(@PathVariable Long merchantId,
                                              @Validated @RequestBody ApplicationApprovalParam approvalParam) {
        try {
            MerMerchant merchant = merchantApplicationService.approveApplication(merchantId, approvalParam);
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("message", "Merchant application approved successfully. Status: Approved (Pending Activation)");
            resultData.put("application_id", merchant.getId());
            resultData.put("new_status", merchant.getStatus());
            return CommonResult.success(resultData);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("An unexpected error occurred during approval.");
        }
    }

    @ApiOperation("Reject Merchant Application")
    @RequestMapping(value = "/applications/{merchantId}/reject", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchant:reject')") // Example permission
    public CommonResult<?> rejectApplication(@PathVariable Long merchantId,
                                             @Validated @RequestBody ApplicationRejectionParam rejectionParam) {
        try {
            MerMerchant merchant = merchantApplicationService.rejectApplication(merchantId, rejectionParam);
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("message", "Merchant application rejected successfully. Status: Rejected");
            resultData.put("application_id", merchant.getId());
            resultData.put("new_status", merchant.getStatus());
            return CommonResult.success(resultData);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("An unexpected error occurred during rejection.");
        }
    }
    
    @ApiOperation("Update Merchant Status (Activate/Deactivate)")
    @RequestMapping(value = "/{merchantId}/status", method = RequestMethod.PUT)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchant:updateStatus')") // Example permission
    public CommonResult<?> updateMerchantStatus(@PathVariable Long merchantId,
                                             @Validated @RequestBody Map<String, Integer> payload) {
        Integer status = payload.get("status");
        if (status == null || (status != 3 && status != 4)) {
             return CommonResult.failed("Invalid status value. Must be 3 (Active) or 4 (Inactive).");
        }
        try {
            MerMerchant merchant = merchantApplicationService.updateMerchantStatus(merchantId, status);
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("message", "Merchant status updated successfully.");
            resultData.put("merchant_id", merchant.getId());
            resultData.put("new_status", merchant.getStatus());
            resultData.put("new_status_description", status == 3 ? "Active" : "Inactive");
            return CommonResult.success(resultData);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("An unexpected error occurred while updating status.");
        }
    }

    // --- Merchant Level Management ---
    @ApiOperation("List All Merchant Levels")
    @RequestMapping(value = "/levels", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchantlevel:read')")
    public CommonResult<List<MerMerchantLevel>> listMerchantLevels() {
        List<MerMerchantLevel> levels = merchantApplicationService.listMerchantLevels();
        return CommonResult.success(levels);
    }

    @ApiOperation("Create Merchant Level")
    @RequestMapping(value = "/levels", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchantlevel:create')")
    public CommonResult<MerMerchantLevel> createMerchantLevel(@Validated @RequestBody MerMerchantLevel merchantLevel) {
        // Basic validation, can be enhanced
        if (merchantLevel.getLevelName() == null || merchantLevel.getLevelName().isEmpty()) {
            return CommonResult.failed("Level name is required.");
        }
        try {
            MerMerchantLevel createdLevel = merchantApplicationService.createMerchantLevel(merchantLevel);
            return CommonResult.success(createdLevel);
        } catch (Exception e) {
            return CommonResult.failed("Failed to create merchant level: " + e.getMessage());
        }
    }
    
    @ApiOperation("Update Merchant Level")
    @RequestMapping(value = "/levels/{levelId}", method = RequestMethod.PUT)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchantlevel:update')")
    public CommonResult<MerMerchantLevel> updateMerchantLevel(@PathVariable Long levelId, @Validated @RequestBody MerMerchantLevel merchantLevel) {
         try {
            MerMerchantLevel updatedLevel = merchantApplicationService.updateMerchantLevel(levelId, merchantLevel);
            if (updatedLevel == null) {
                return CommonResult.failed("Merchant level not found or update failed.");
            }
            return CommonResult.success(updatedLevel);
        } catch (Exception e) {
            return CommonResult.failed("Failed to update merchant level: " + e.getMessage());
        }
    }

    // --- Merchant Package Management ---
    @ApiOperation("Assign or Update Merchant Package")
    @RequestMapping(value = "/{merchantId}/package", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchantpackage:assign')")
    public CommonResult<MerMerchantPackage> assignOrUpdateMerchantPackage(@PathVariable Long merchantId, @Validated @RequestBody MerchantPackageAssignParam packageParam) {
        try {
            MerMerchantPackage assignedPackage = merchantApplicationService.assignOrUpdateMerchantPackage(merchantId, packageParam);
            return CommonResult.success(assignedPackage, "Merchant package assigned/updated successfully.");
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("An unexpected error occurred while assigning package.");
        }
    }

    @ApiOperation("Get Merchant Package History")
    @RequestMapping(value = "/{merchantId}/packages", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:merchantpackage:read')")
    public CommonResult<List<MerMerchantPackage>> getMerchantPackageHistory(@PathVariable Long merchantId) {
        List<MerMerchantPackage> packageHistory = merchantApplicationService.getMerchantPackageHistory(merchantId);
        return CommonResult.success(packageHistory);
    }
}
