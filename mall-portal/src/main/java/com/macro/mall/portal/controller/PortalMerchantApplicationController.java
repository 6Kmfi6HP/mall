package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.MerMerchant;
import com.macro.mall.portal.domain.MerchantRegistrationParam;
import com.macro.mall.portal.service.PortalMerchantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Api(tags = "PortalMerchantApplicationController", description = "Merchant Registration Application Management (Portal)")
@RequestMapping("/merchants")
public class PortalMerchantApplicationController {

    @Autowired
    private PortalMerchantApplicationService merchantApplicationService;

    @ApiOperation("Submit Merchant Registration Application")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<?> register(@Validated @RequestBody MerchantRegistrationParam registrationParam) {
        try {
            MerMerchant merchant = merchantApplicationService.register(registrationParam);
            if (merchant != null && merchant.getId() != null) {
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("id", merchant.getId());
                resultData.put("name", merchant.getName());
                resultData.put("contactName", merchant.getContactName());
                resultData.put("contactPhone", merchant.getContactPhone());
                resultData.put("contactEmail", merchant.getContactEmail());
                resultData.put("address", merchant.getAddress());
                resultData.put("status", merchant.getStatus());
                resultData.put("createTime", merchant.getCreateTime());
                resultData.put("message", "Merchant registration application submitted successfully. Application ID: " + merchant.getId());
                return CommonResult.created(resultData);
            } else {
                return CommonResult.failed("Failed to submit merchant application.");
            }
        } catch (IllegalArgumentException e) {
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return CommonResult.failed("An unexpected error occurred during registration.");
        }
    }

    // GET /merchants/applications/{applicationId}/status endpoint would go here
    // This requires more DTOs and service logic to fetch combined status from mer_merchant and mer_merchant_qualification
    // For now, per subtask, focusing on registration.
}
