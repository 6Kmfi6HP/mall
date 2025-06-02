package com.macro.mall.oms.service.impl;

import com.macro.mall.mapper.MerMerchantPackageMapper;
import com.macro.mall.mapper.OmsOrderItemCommissionMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.*;
import com.macro.mall.oms.service.OmsCommissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class OmsCommissionServiceImpl implements OmsCommissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmsCommissionServiceImpl.class);

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private MerMerchantPackageMapper merMerchantPackageMapper;

    @Autowired
    private OmsOrderItemCommissionMapper omsOrderItemCommissionMapper;

    @Transactional
    @Override
    public void calculateAndRecordCommissionsForOrder(Long orderId) {
        LOGGER.info("Starting commission calculation for orderId: {}", orderId);

        OmsOrder order = omsOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            LOGGER.warn("Order not found for orderId: {}. Skipping commission calculation.", orderId);
            return;
        }

        // Assuming status 3 means "Completed" and is eligible for commission calculation
        if (order.getStatus() != 3) {
            LOGGER.info("Order status for orderId: {} is not 'Completed' (status: {}). Skipping commission calculation.", orderId, order.getStatus());
            return;
        }

        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItems = omsOrderItemMapper.selectByExample(orderItemExample);

        if (CollectionUtils.isEmpty(orderItems)) {
            LOGGER.info("No order items found for orderId: {}. Skipping commission calculation.", orderId);
            return;
        }

        for (OmsOrderItem item : orderItems) {
            if (item.getMerchantId() == null) {
                LOGGER.warn("OrderItemId: {} in OrderId: {} does not have a merchantId. Skipping commission for this item.", item.getId(), orderId);
                continue;
            }

            // Determine the date to use for finding the active package. Using order creation time.
            Date relevantDateForPackage = order.getCreateTime(); 
            if (relevantDateForPackage == null) {
                 LOGGER.warn("OrderId: {} has no createTime. Using current date for package lookup for OrderItemId: {}", orderId, item.getId());
                 relevantDateForPackage = new Date();
            }


            MerMerchantPackageExample packageExample = new MerMerchantPackageExample();
            packageExample.createCriteria()
                    .andMerchantIdEqualTo(item.getMerchantId())
                    .andIsActiveEqualTo(true)
                    .andStartDateLessThanOrEqualTo(relevantDateForPackage)
                    .andEndDateGreaterThanOrEqualTo(relevantDateForPackage);
            packageExample.setOrderByClause("start_date DESC"); // Prioritize the latest started package if overlaps

            List<MerMerchantPackage> activePackages = merMerchantPackageMapper.selectByExample(packageExample);

            if (CollectionUtils.isEmpty(activePackages)) {
                LOGGER.warn("No active commission package found for merchantId: {} at the time of order ({}) for orderItemId: {}. Skipping commission.",
                        item.getMerchantId(), relevantDateForPackage, item.getId());
                continue;
            }

            MerMerchantPackage currentPackage = activePackages.get(0); // Get the most relevant one
            if (activePackages.size() > 1) {
                 LOGGER.warn("Multiple active packages found for merchantId: {} for orderItemId: {}. Using package with ID: {}", 
                    item.getMerchantId(), item.getId(), currentPackage.getId());
            }


            BigDecimal commissionRate = currentPackage.getCommissionRate();
            if (commissionRate == null) {
                LOGGER.warn("Commission rate is null for packageId: {} (merchantId: {}). Skipping commission for orderItemId: {}.",
                        currentPackage.getId(), item.getMerchantId(), item.getId());
                continue;
            }

            BigDecimal baseAmount = item.getRealAmount(); // real_amount is the final amount paid for the item after discounts
            if (baseAmount == null) {
                LOGGER.warn("RealAmount is null for orderItemId: {}. Skipping commission for this item.", item.getId());
                continue;
            }

            BigDecimal commissionAmount = baseAmount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

            OmsOrderItemCommission commissionRecord = new OmsOrderItemCommission();
            commissionRecord.setOrderItemId(item.getId());
            commissionRecord.setMerchantId(item.getMerchantId());
            commissionRecord.setProductId(item.getProductId());
            commissionRecord.setOrderId(orderId);
            commissionRecord.setCommissionRate(commissionRate);
            commissionRecord.setCommissionAmount(commissionAmount);
            commissionRecord.setCalculatedTime(new Date());

            try {
                omsOrderItemCommissionMapper.insertSelective(commissionRecord);
                LOGGER.info("Successfully recorded commission for orderItemId: {}, merchantId: {}, amount: {}",
                        item.getId(), item.getMerchantId(), commissionAmount);
            } catch (Exception e) {
                LOGGER.error("Error inserting commission record for orderItemId: {}: {}", item.getId(), e.getMessage(), e);
                // Depending on requirements, might re-throw to rollback transaction or just log and continue
            }
        }
        LOGGER.info("Finished commission calculation for orderId: {}", orderId);
    }
}
