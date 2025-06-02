package com.macro.mall.oms.service;

public interface OmsCommissionService {

    /**
     * Calculates and records commissions for all eligible items in a given order.
     * This method should be called when an order is confirmed as "Completed".
     *
     * @param orderId The ID of the order for which to calculate commissions.
     */
    void calculateAndRecordCommissionsForOrder(Long orderId);
}
