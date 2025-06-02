package com.macro.mall.oms.service.impl;

import com.macro.mall.mapper.MerMerchantPackageMapper;
import com.macro.mall.mapper.OmsOrderItemCommissionMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.*;
import com.macro.mall.oms.service.OmsCommissionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OmsCommissionServiceImplTest {

    @Mock
    private OmsOrderMapper omsOrderMapper;
    @Mock
    private OmsOrderItemMapper omsOrderItemMapper;
    @Mock
    private MerMerchantPackageMapper merMerchantPackageMapper;
    @Mock
    private OmsOrderItemCommissionMapper omsOrderItemCommissionMapper;

    @InjectMocks
    private OmsCommissionServiceImpl omsCommissionService;

    private OmsOrder testOrder;
    private OmsOrderItem testItem1;
    private OmsOrderItem testItem2;
    private MerMerchantPackage testPackageMerchant1;
    private MerMerchantPackage testPackageMerchant2;

    private final Long ORDER_ID = 1L;
    private final Long MERCHANT_ID_1 = 101L;
    private final Long MERCHANT_ID_2 = 102L;
    private final Long ITEM_ID_1 = 1001L;
    private final Long ITEM_ID_2 = 1002L;
    private final Long PRODUCT_ID_1 = 2001L;
    private final Long PRODUCT_ID_2 = 2002L;


    @BeforeEach
    void setUp() {
        testOrder = new OmsOrder();
        testOrder.setId(ORDER_ID);
        testOrder.setStatus(3); // Completed
        testOrder.setCreateTime(new Date());

        testItem1 = new OmsOrderItem();
        testItem1.setId(ITEM_ID_1);
        testItem1.setOrderId(ORDER_ID);
        testItem1.setMerchantId(MERCHANT_ID_1);
        testItem1.setProductId(PRODUCT_ID_1);
        testItem1.setRealAmount(new BigDecimal("100.00"));

        testItem2 = new OmsOrderItem();
        testItem2.setId(ITEM_ID_2);
        testItem2.setOrderId(ORDER_ID);
        testItem2.setMerchantId(MERCHANT_ID_2);
        testItem2.setProductId(PRODUCT_ID_2);
        testItem2.setRealAmount(new BigDecimal("200.00"));
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(testOrder.getCreateTime());
        cal.add(Calendar.MONTH, -1);
        Date packageStartDate = cal.getTime();
        cal.add(Calendar.MONTH, 2);
        Date packageEndDate = cal.getTime();

        testPackageMerchant1 = new MerMerchantPackage();
        testPackageMerchant1.setId(1L);
        testPackageMerchant1.setMerchantId(MERCHANT_ID_1);
        testPackageMerchant1.setCommissionRate(new BigDecimal("0.05")); // 5%
        testPackageMerchant1.setIsActive(true);
        testPackageMerchant1.setStartDate(packageStartDate);
        testPackageMerchant1.setEndDate(packageEndDate);
        
        testPackageMerchant2 = new MerMerchantPackage();
        testPackageMerchant2.setId(2L);
        testPackageMerchant2.setMerchantId(MERCHANT_ID_2);
        testPackageMerchant2.setCommissionRate(new BigDecimal("0.10")); // 10%
        testPackageMerchant2.setIsActive(true);
        testPackageMerchant2.setStartDate(packageStartDate);
        testPackageMerchant2.setEndDate(packageEndDate);
    }

    @Test
    void testCalculateAndRecordCommissions_Success_SingleItem() {
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        ArgumentCaptor<OmsOrderItemCommission> captor = ArgumentCaptor.forClass(OmsOrderItemCommission.class);
        verify(omsOrderItemCommissionMapper, times(1)).insertSelective(captor.capture());
        
        OmsOrderItemCommission capturedCommission = captor.getValue();
        assertEquals(ITEM_ID_1, capturedCommission.getOrderItemId());
        assertEquals(MERCHANT_ID_1, capturedCommission.getMerchantId());
        assertEquals(PRODUCT_ID_1, capturedCommission.getProductId());
        assertEquals(ORDER_ID, capturedCommission.getOrderId());
        assertEquals(0.05, capturedCommission.getCommissionRate().doubleValue(), 0.001);
        assertEquals(5.00, capturedCommission.getCommissionAmount().doubleValue(), 0.001); // 100.00 * 0.05
        assertNotNull(capturedCommission.getCalculatedTime());
    }

    @Test
    void testCalculateAndRecordCommissions_Success_MultipleItems_SameMerchant() {
        testItem2.setMerchantId(MERCHANT_ID_1); // Both items for same merchant
        List<OmsOrderItem> items = new ArrayList<>();
        items.add(testItem1);
        items.add(testItem2);

        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class))).thenReturn(items);
        // Mock will be called twice for the same merchant but should return the same package
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1));
        
        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        ArgumentCaptor<OmsOrderItemCommission> captor = ArgumentCaptor.forClass(OmsOrderItemCommission.class);
        verify(omsOrderItemCommissionMapper, times(2)).insertSelective(captor.capture());
        List<OmsOrderItemCommission> capturedCommissions = captor.getAllValues();

        assertEquals(2, capturedCommissions.size());
        // Item 1
        assertEquals(ITEM_ID_1, capturedCommissions.get(0).getOrderItemId());
        assertEquals(MERCHANT_ID_1, capturedCommissions.get(0).getMerchantId());
        assertEquals(0.05, capturedCommissions.get(0).getCommissionRate().doubleValue(), 0.001);
        assertEquals(5.00, capturedCommissions.get(0).getCommissionAmount().doubleValue(), 0.001);
        // Item 2 (same merchant, same rate)
        assertEquals(ITEM_ID_2, capturedCommissions.get(1).getOrderItemId());
        assertEquals(MERCHANT_ID_1, capturedCommissions.get(1).getMerchantId());
        assertEquals(0.05, capturedCommissions.get(1).getCommissionRate().doubleValue(), 0.001);
        assertEquals(10.00, capturedCommissions.get(1).getCommissionAmount().doubleValue(), 0.001); // 200.00 * 0.05
    }

    @Test
    void testCalculateAndRecordCommissions_Success_MultipleItems_DifferentMerchants() {
        List<OmsOrderItem> items = new ArrayList<>();
        items.add(testItem1); // Merchant 1
        items.add(testItem2); // Merchant 2

        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class))).thenReturn(items);
        
        // Mock package fetching for merchant 1 then merchant 2
        when(merMerchantPackageMapper.selectByExample(argThat(ex -> ex.getOredCriteria().get(0).getCriteria().stream().anyMatch(c -> c.getCondition().equals("merchant_id =") && c.getValue().equals(MERCHANT_ID_1)))))
            .thenReturn(Collections.singletonList(testPackageMerchant1));
        when(merMerchantPackageMapper.selectByExample(argThat(ex -> ex.getOredCriteria().get(0).getCriteria().stream().anyMatch(c -> c.getCondition().equals("merchant_id =") && c.getValue().equals(MERCHANT_ID_2)))))
            .thenReturn(Collections.singletonList(testPackageMerchant2));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        ArgumentCaptor<OmsOrderItemCommission> captor = ArgumentCaptor.forClass(OmsOrderItemCommission.class);
        verify(omsOrderItemCommissionMapper, times(2)).insertSelective(captor.capture());
        List<OmsOrderItemCommission> capturedCommissions = captor.getAllValues();
        
        assertEquals(2, capturedCommissions.size());
        // Item 1 - Merchant 1
        OmsOrderItemCommission comm1 = capturedCommissions.stream().filter(c -> c.getOrderItemId().equals(ITEM_ID_1)).findFirst().orElse(null);
        assertNotNull(comm1);
        assertEquals(MERCHANT_ID_1, comm1.getMerchantId());
        assertEquals(0.05, comm1.getCommissionRate().doubleValue(), 0.001);
        assertEquals(5.00, comm1.getCommissionAmount().doubleValue(), 0.001);
        // Item 2 - Merchant 2
        OmsOrderItemCommission comm2 = capturedCommissions.stream().filter(c -> c.getOrderItemId().equals(ITEM_ID_2)).findFirst().orElse(null);
        assertNotNull(comm2);
        assertEquals(MERCHANT_ID_2, comm2.getMerchantId());
        assertEquals(0.10, comm2.getCommissionRate().doubleValue(), 0.001);
        assertEquals(20.00, comm2.getCommissionAmount().doubleValue(), 0.001); // 200.00 * 0.10
    }

    @Test
    void testCalculateAndRecordCommissions_OrderItem_NoMerchantId() {
        testItem1.setMerchantId(null);
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        verify(merMerchantPackageMapper, never()).selectByExample(any());
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
        // Add log verification if testing framework supports it
    }

    @Test
    void testCalculateAndRecordCommissions_NoActivePackageForMerchant() {
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.emptyList()); // No active package

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
        // Add log verification
    }

    @Test
    void testCalculateAndRecordCommissions_OrderNotCompleted() {
        testOrder.setStatus(1); // Not completed
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        verify(omsOrderItemMapper, never()).selectByExample(any());
        verify(merMerchantPackageMapper, never()).selectByExample(any());
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }
    
    @Test
    void testCalculateAndRecordCommissions_OrderNotFound() {
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(null);
        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemMapper, never()).selectByExample(any());
    }

    @Test
    void testCalculateAndRecordCommissions_NoOrderItems() {
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.emptyList());
        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(merMerchantPackageMapper, never()).selectByExample(any());
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }


    @Test
    void testCalculateAndRecordCommissions_ZeroPriceItem() {
        testItem1.setRealAmount(BigDecimal.ZERO);
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);

        ArgumentCaptor<OmsOrderItemCommission> captor = ArgumentCaptor.forClass(OmsOrderItemCommission.class);
        verify(omsOrderItemCommissionMapper).insertSelective(captor.capture());
        assertEquals(0.00, captor.getValue().getCommissionAmount().doubleValue(), 0.001);
    }
    
    @Test
    void testCalculateAndRecordCommissions_NullRealAmountItem() {
        testItem1.setRealAmount(null);
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1)); // Package fetch will still happen

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }


    @Test
    void testCalculateAndRecordCommissions_PackageDatesScenario_OrderDateWithinPackage() {
        // Default setup has order date within package validity
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemCommissionMapper, times(1)).insertSelective(any());
    }

    @Test
    void testCalculateAndRecordCommissions_PackageDatesScenario_OrderDateBeforePackageStart() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(testPackageMerchant1.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, -1); // Order date one day before package start
        testOrder.setCreateTime(cal.getTime());

        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.emptyList()); // Expect no package

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }

    @Test
    void testCalculateAndRecordCommissions_PackageDatesScenario_OrderDateAfterPackageEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(testPackageMerchant1.getEndDate());
        cal.add(Calendar.DAY_OF_YEAR, 1); // Order date one day after package end
        testOrder.setCreateTime(cal.getTime());

        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.emptyList()); // Expect no package

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }
    
    @Test
    void testCalculateAndRecordCommissions_NullCommissionRateInPackage() {
        testPackageMerchant1.setCommissionRate(null);
        when(omsOrderMapper.selectByPrimaryKey(ORDER_ID)).thenReturn(testOrder);
        when(omsOrderItemMapper.selectByExample(any(OmsOrderItemExample.class)))
                .thenReturn(Collections.singletonList(testItem1));
        when(merMerchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
                .thenReturn(Collections.singletonList(testPackageMerchant1));

        omsCommissionService.calculateAndRecordCommissionsForOrder(ORDER_ID);
        verify(omsOrderItemCommissionMapper, never()).insertSelective(any());
    }
}
