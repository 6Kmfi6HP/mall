package com.macro.mall.portal.service;

import com.macro.mall.model.MerMerchant;
import com.macro.mall.portal.domain.MerchantRegistrationParam;

public interface PortalMerchantApplicationService {

    /**
     * Submits a merchant registration application.
     *
     * @param param Merchant registration parameters
     * @return The created merchant application (MerMerchant object with current status)
     */
    MerMerchant register(MerchantRegistrationParam param);

    // Potentially, a method to get application status could be added here later if needed by the portal user
    // MerApplicationStatusDto getApplicationStatus(Long applicationId); 
}
