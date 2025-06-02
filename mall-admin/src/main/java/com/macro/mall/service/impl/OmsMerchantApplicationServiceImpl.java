package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.MerMerchantLevelMapper;
import com.macro.mall.mapper.MerMerchantMapper;
import com.macro.mall.mapper.MerMerchantPackageMapper;
import com.macro.mall.mapper.MerMerchantQualificationMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsMerchantApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmsMerchantApplicationServiceImpl implements OmsMerchantApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmsMerchantApplicationServiceImpl.class);

    @Autowired
    private MerMerchantMapper merchantMapper;

    @Autowired
    private MerMerchantQualificationMapper merchantQualificationMapper;
    
    @Autowired
    private MerMerchantLevelMapper merchantLevelMapper;

    @Autowired
    private MerMerchantPackageMapper merchantPackageMapper;

    @Override
    public List<MerchantApplicationDto> listApplications(String name, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        MerMerchantExample example = new MerMerchantExample();
        MerMerchantExample.Criteria criteria = example.createCriteria();
        if (name != null && !name.isEmpty()) {
            criteria.andNameLike("%" + name + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        example.setOrderByClause("create_time desc");
        List<MerMerchant> merchants = merchantMapper.selectByExample(example);
        
        return merchants.stream().map(merchant -> {
            MerchantApplicationDto dto = new MerchantApplicationDto();
            BeanUtils.copyProperties(merchant, dto);
            // To get submission_date, we might need to query mer_merchant_qualification
            // For simplicity, we'll use merchant's create_time as submission_date for now
            // In a real scenario, you might want to fetch from mer_merchant_qualification
            MerMerchantQualificationExample qualExample = new MerMerchantQualificationExample();
            qualExample.createCriteria().andMerchantIdEqualTo(merchant.getId());
            List<MerMerchantQualification> quals = merchantQualificationMapper.selectByExample(qualExample);
            if (!CollectionUtils.isEmpty(quals) && quals.get(0).getSubmissionDate() != null) {
                dto.setSubmissionDate(quals.get(0).getSubmissionDate());
            } else {
                 dto.setSubmissionDate(merchant.getCreateTime());
            }
            // statusDescription is handled by the DTO's getter
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public MerchantApplicationDetailDto getApplicationDetail(Long merchantId) {
        MerMerchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            return null;
        }
        MerchantApplicationDetailDto detailDto = new MerchantApplicationDetailDto();
        BeanUtils.copyProperties(merchant, detailDto);

        MerMerchantQualificationExample qualExample = new MerMerchantQualificationExample();
        qualExample.createCriteria().andMerchantIdEqualTo(merchantId);
        List<MerMerchantQualification> qualifications = merchantQualificationMapper.selectByExample(qualExample);
        if (!CollectionUtils.isEmpty(qualifications)) {
            MerMerchantQualification qualification = qualifications.get(0);
            detailDto.setQualificationId(qualification.getId());
            BeanUtils.copyProperties(qualification, detailDto);
            detailDto.setQualificationSubmissionDate(qualification.getSubmissionDate());
            detailDto.setQualificationReviewDate(qualification.getReviewDate());
            detailDto.setQualificationReviewStatus(qualification.getReviewStatus());
            detailDto.setQualificationReviewNotes(qualification.getReviewNotes());
        }
        
        // Fetch current package
        MerMerchantPackageExample packageExample = new MerMerchantPackageExample();
        packageExample.createCriteria().andMerchantIdEqualTo(merchantId).andIsActiveEqualTo(true);
        packageExample.setOrderByClause("create_time DESC"); // Get the latest active one
        List<MerMerchantPackage> packages = merchantPackageMapper.selectByExample(packageExample);
        if (!CollectionUtils.isEmpty(packages)) {
            MerMerchantPackage currentPackage = packages.get(0);
            MerMerchantLevel level = merchantLevelMapper.selectByPrimaryKey(currentPackage.getLevelId());
            if (level != null) {
                detailDto.setCurrentPackageLevelName(level.getLevelName());
            }
            detailDto.setCurrentCommissionRate(currentPackage.getCommissionRate() != null ? currentPackage.getCommissionRate().doubleValue() : null);
            detailDto.setCurrentPackageStartDate(currentPackage.getStartDate());
            detailDto.setCurrentPackageEndDate(currentPackage.getEndDate());
            detailDto.setCurrentPackageIsActive(currentPackage.getIsActive());
        }

        return detailDto;
    }

    @Transactional
    @Override
    public MerMerchant approveApplication(Long merchantId, ApplicationApprovalParam approvalParam) {
        MerMerchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant application not found.");
        }
        if (merchant.getStatus() != 0) { // Not PENDING
            throw new IllegalStateException("Application is not in a pending state, cannot approve.");
        }

        MerMerchantQualificationExample qualExample = new MerMerchantQualificationExample();
        qualExample.createCriteria().andMerchantIdEqualTo(merchantId);
        List<MerMerchantQualification> qualifications = merchantQualificationMapper.selectByExample(qualExample);
        if (CollectionUtils.isEmpty(qualifications)) {
            throw new IllegalStateException("Merchant qualification data not found.");
        }
        MerMerchantQualification qualification = qualifications.get(0);

        // Update merchant status
        merchant.setStatus(1); // 1->Approved (Pending Activation)
        merchant.setUpdateTime(new Date());
        merchantMapper.updateByPrimaryKeySelective(merchant);

        // Update qualification status
        qualification.setReviewStatus(1); // 1->Approved
        qualification.setReviewNotes(approvalParam.getReviewNotes());
        qualification.setReviewDate(new Date());
        merchantQualificationMapper.updateByPrimaryKeySelective(qualification);

        // Assign initial package
        MerMerchantLevel selectedLevel = merchantLevelMapper.selectByPrimaryKey(approvalParam.getSelectedPackageLevelId());
        if (selectedLevel == null) {
            throw new IllegalArgumentException("Selected merchant level not found.");
        }

        MerMerchantPackage newPackage = new MerMerchantPackage();
        newPackage.setMerchantId(merchantId);
        newPackage.setLevelId(selectedLevel.getId());
        
        // Set commission rate based on level (example logic)
        // This should be more robust, perhaps from level configuration or a predefined map
        if ("基础版".equals(selectedLevel.getLevelName())) {
            newPackage.setCommissionRate(new BigDecimal("0.05"));
        } else if ("标准版".equals(selectedLevel.getLevelName())) {
            newPackage.setCommissionRate(new BigDecimal("0.03"));
        } else if ("旗舰版".equals(selectedLevel.getLevelName())) {
            newPackage.setCommissionRate(new BigDecimal("0.02"));
        } else {
             // Default or throw error if level name doesn't match expected
            newPackage.setCommissionRate(new BigDecimal("0.10")); // Default to a higher rate or handle error
        }

        Date now = new Date();
        newPackage.setStartDate(now);
        // Set end date, e.g., 1 year from now
        newPackage.setEndDate(new Date(now.getTime() + (365L * 24 * 60 * 60 * 1000)));
        newPackage.setIsActive(true); // Initially active
        newPackage.setCreateTime(now);
        newPackage.setUpdateTime(now);
        merchantPackageMapper.insertSelective(newPackage);
        
        LOGGER.info("Merchant application {} approved. Assigned level: {}", merchantId, selectedLevel.getLevelName());
        return merchant;
    }

    @Transactional
    @Override
    public MerMerchant rejectApplication(Long merchantId, ApplicationRejectionParam rejectionParam) {
        MerMerchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant application not found.");
        }
         if (merchant.getStatus() != 0) { // Not PENDING
            throw new IllegalStateException("Application is not in a pending state, cannot reject.");
        }

        MerMerchantQualificationExample qualExample = new MerMerchantQualificationExample();
        qualExample.createCriteria().andMerchantIdEqualTo(merchantId);
        List<MerMerchantQualification> qualifications = merchantQualificationMapper.selectByExample(qualExample);
         if (CollectionUtils.isEmpty(qualifications)) {
            throw new IllegalStateException("Merchant qualification data not found.");
        }
        MerMerchantQualification qualification = qualifications.get(0);

        // Update merchant status
        merchant.setStatus(2); // 2->Rejected
        merchant.setUpdateTime(new Date());
        merchantMapper.updateByPrimaryKeySelective(merchant);

        // Update qualification status
        qualification.setReviewStatus(2); // 2->Rejected
        qualification.setReviewNotes(rejectionParam.getReviewNotes());
        qualification.setReviewDate(new Date());
        merchantQualificationMapper.updateByPrimaryKeySelective(qualification);
        
        LOGGER.info("Merchant application {} rejected. Reason: {}", merchantId, rejectionParam.getReviewNotes());
        return merchant;
    }
    
    @Transactional
    @Override
    public MerMerchant updateMerchantStatus(Long merchantId, Integer status) {
        MerMerchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant not found.");
        }
        // Only allow activation/deactivation for approved merchants
        if (merchant.getStatus() != 1 && merchant.getStatus() != 3 && merchant.getStatus() != 4) {
            throw new IllegalStateException("Merchant is not in a state that allows status change to Active/Inactive.");
        }
        if (status != 3 && status != 4) { // 3->Active, 4->Inactive
            throw new IllegalArgumentException("Invalid status value for activation/deactivation.");
        }

        merchant.setStatus(status);
        merchant.setUpdateTime(new Date());
        merchantMapper.updateByPrimaryKeySelective(merchant);
        LOGGER.info("Merchant {} status updated to {}", merchantId, status == 3 ? "Active" : "Inactive");
        return merchant;
    }
    
    @Override
    public List<MerMerchantLevel> listMerchantLevels() {
        return merchantLevelMapper.selectByExample(new MerMerchantLevelExample());
    }

    @Override
    public MerMerchantLevel createMerchantLevel(MerMerchantLevel merchantLevel) {
        merchantLevel.setCreateTime(new Date());
        merchantLevel.setUpdateTime(new Date());
        merchantLevelMapper.insertSelective(merchantLevel);
        return merchantLevel;
    }
    
    @Override
    public MerMerchantLevel updateMerchantLevel(Long levelId, MerMerchantLevel merchantLevel) {
        merchantLevel.setId(levelId);
        merchantLevel.setUpdateTime(new Date());
        merchantLevelMapper.updateByPrimaryKeySelective(merchantLevel);
        return merchantLevelMapper.selectByPrimaryKey(levelId);
    }

    @Transactional
    @Override
    public MerMerchantPackage assignOrUpdateMerchantPackage(Long merchantId, MerchantPackageAssignParam packageParam) {
        MerMerchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant not found with ID: " + merchantId);
        }
        MerMerchantLevel level = merchantLevelMapper.selectByPrimaryKey(packageParam.getLevelId());
        if (level == null) {
            throw new IllegalArgumentException("Merchant Level not found with ID: " + packageParam.getLevelId());
        }

        // Deactivate existing active packages for this merchant if any
        MerMerchantPackageExample existingActiveExample = new MerMerchantPackageExample();
        existingActiveExample.createCriteria().andMerchantIdEqualTo(merchantId).andIsActiveEqualTo(true);
        List<MerMerchantPackage> activePackages = merchantPackageMapper.selectByExample(existingActiveExample);
        for (MerMerchantPackage activePackage : activePackages) {
            activePackage.setIsActive(false);
            activePackage.setUpdateTime(new Date());
            merchantPackageMapper.updateByPrimaryKeySelective(activePackage);
        }

        // Create new package
        MerMerchantPackage newPackage = new MerMerchantPackage();
        BeanUtils.copyProperties(packageParam, newPackage);
        newPackage.setMerchantId(merchantId);
        newPackage.setCreateTime(new Date());
        newPackage.setUpdateTime(new Date());
        
        merchantPackageMapper.insertSelective(newPackage);
        LOGGER.info("Assigned new package (ID: {}) to merchant ID: {}. Level ID: {}", newPackage.getId(), merchantId, packageParam.getLevelId());
        return newPackage;
    }

    @Override
    public List<MerMerchantPackage> getMerchantPackageHistory(Long merchantId) {
        MerMerchantPackageExample example = new MerMerchantPackageExample();
        example.createCriteria().andMerchantIdEqualTo(merchantId);
        example.setOrderByClause("create_time DESC");
        return merchantPackageMapper.selectByExample(example);
    }
}
