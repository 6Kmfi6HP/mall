package com.macro.mall.service;

import com.macro.mall.dto.ApplicationApprovalParam;
import com.macro.mall.dto.ApplicationRejectionParam;
import com.macro.mall.dto.MerchantApplicationDetailDto;
import com.macro.mall.dto.MerchantApplicationDto;
import com.macro.mall.model.MerMerchant; // Assuming this is the primary entity for status update
import com.macro.mall.model.MerMerchantPackage;

import java.util.List;

public interface OmsMerchantApplicationService {

    /**
     * List merchant applications with pagination and filtering.
     * @param name Filter by merchant name (optional)
     * @param status Filter by application status (optional)
     * @param pageSize Page size
     * @param pageNum Page number
     * @return List of merchant applications
     */
    List<MerchantApplicationDto> listApplications(String name, Integer status, Integer pageSize, Integer pageNum);

    /**
     * Get details of a specific merchant application.
     * @param merchantId ID of the merchant application
     * @return Detailed DTO of the application
     */
    MerchantApplicationDetailDto getApplicationDetail(Long merchantId);

    /**
     * Approve a merchant application.
     * @param merchantId ID of the merchant application
     * @param approvalParam Parameters for approval (notes, package level)
     * @return Updated MerMerchant entity
     */
    MerMerchant approveApplication(Long merchantId, ApplicationApprovalParam approvalParam);

    /**
     * Reject a merchant application.
     * @param merchantId ID of the merchant application
     * @param rejectionParam Parameters for rejection (notes)
     * @return Updated MerMerchant entity
     */
    MerMerchant rejectApplication(Long merchantId, ApplicationRejectionParam rejectionParam);
    
    /**
     * Update merchant status (activate/deactivate).
     * @param merchantId ID of the merchant
     * @param status New status (3 for Active, 4 for Inactive)
     * @return Updated MerMerchant entity
     */
    MerMerchant updateMerchantStatus(Long merchantId, Integer status);

    /**
     * Assigns or updates a merchant's package.
     * @param merchantId ID of the merchant
     * @param packageParam Parameters for the package
     * @return The created or updated merchant package
     */
    MerMerchantPackage assignOrUpdateMerchantPackage(Long merchantId, com.macro.mall.dto.MerchantPackageAssignParam packageParam); // Need to create MerchantPackageAssignParam DTO

    /**
     * Retrieves package history for a merchant.
     * @param merchantId ID of the merchant
     * @return List of merchant packages
     */
    List<MerMerchantPackage> getMerchantPackageHistory(Long merchantId);

     /**
     * Retrieves all merchant levels.
     * @return List of merchant levels
     */
    List<com.macro.mall.model.MerMerchantLevel> listMerchantLevels(); // Using model directly for simplicity

    /**
     * Creates a new merchant level.
     * @param merchantLevel Merchant level details
     * @return Created merchant level
     */
    com.macro.mall.model.MerMerchantLevel createMerchantLevel(com.macro.mall.model.MerMerchantLevel merchantLevel);

    /**
     * Updates an existing merchant level.
     * @param levelId ID of the level to update
     * @param merchantLevel Merchant level details to update
     * @return Updated merchant level
     */
    com.macro.mall.model.MerMerchantLevel updateMerchantLevel(Long levelId, com.macro.mall.model.MerMerchantLevel merchantLevel);

}
