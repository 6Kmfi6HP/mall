package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.MerMerchantMapper;
import com.macro.mall.mapper.MerMerchantQualificationMapper;
import com.macro.mall.model.MerMerchant;
import com.macro.mall.model.MerMerchantQualification;
import com.macro.mall.portal.domain.MerchantQualificationParam;
import com.macro.mall.portal.domain.MerchantRegistrationParam;
import com.macro.mall.portal.service.PortalMerchantApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Service
public class PortalMerchantApplicationServiceImpl implements PortalMerchantApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalMerchantApplicationServiceImpl.class);

    @Autowired
    private MerMerchantMapper merchantMapper;

    @Autowired
    private MerMerchantQualificationMapper merchantQualificationMapper;

    @Transactional
    @Override
    public MerMerchant register(MerchantRegistrationParam param) {
        // 1. Create MerMerchant entity
        MerMerchant merchant = new MerMerchant();
        BeanUtils.copyProperties(param, merchant);
        merchant.setStatus(0); // 0->Pending
        merchant.setCreateTime(new Date());
        merchant.setUpdateTime(new Date());
        
        // Basic validation (can be enhanced with @Valid in controller and more complex checks here)
        if (param.getName() == null || param.getName().isEmpty()) {
            throw new IllegalArgumentException("Merchant name cannot be empty.");
        }
        // Add more validations as needed

        merchantMapper.insertSelective(merchant);
        LOGGER.info("Inserted new merchant application with ID: {}", merchant.getId());


        // 2. Create MerMerchantQualification entity
        if (merchant.getId() == null) {
             LOGGER.error("Merchant ID is null after insert, cannot proceed with qualification for merchant: {}", param.getName());
            // This should ideally not happen if `useGeneratedKeys` is true and DB is configured correctly.
            throw new RuntimeException("Failed to create merchant application, merchant ID not generated.");
        }
        
        MerchantQualificationParam qualParam = param.getQualifications();
        if (qualParam == null) {
            throw new IllegalArgumentException("Merchant qualifications cannot be null.");
        }

        MerMerchantQualification qualification = new MerMerchantQualification();
        BeanUtils.copyProperties(qualParam, qualification);
        qualification.setMerchantId(merchant.getId());
        qualification.setReviewStatus(0); // 0->Pending
        qualification.setSubmissionDate(new Date());
        
        merchantQualificationMapper.insertSelective(qualification);
        LOGGER.info("Inserted new merchant qualification for merchant ID: {}", merchant.getId());

        return merchant;
    }
}
