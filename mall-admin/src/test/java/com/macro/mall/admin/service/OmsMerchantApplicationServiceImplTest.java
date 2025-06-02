package com.macro.mall.admin.service;

import com.github.pagehelper.PageInfo; // Assuming PageHelper is used and will be on classpath for CommonPage
import com.macro.mall.dto.*;
import com.macro.mall.mapper.MerMerchantLevelMapper;
import com.macro.mall.mapper.MerMerchantMapper;
import com.macro.mall.mapper.MerMerchantPackageMapper;
import com.macro.mall.mapper.MerMerchantQualificationMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.impl.OmsMerchantApplicationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OmsMerchantApplicationServiceImplTest {

    @Mock
    private MerMerchantMapper merchantMapper;
    @Mock
    private MerMerchantQualificationMapper merchantQualificationMapper;
    @Mock
    private MerMerchantLevelMapper merchantLevelMapper;
    @Mock
    private MerMerchantPackageMapper merchantPackageMapper;

    @InjectMocks
    private OmsMerchantApplicationServiceImpl merchantApplicationService;

    private MerMerchant testMerchant;
    private MerMerchantQualification testQualification;
    private MerMerchantLevel testLevelBasic;
    private MerMerchantLevel testLevelStandard;

    @BeforeEach
    void setUp() {
        testMerchant = new MerMerchant();
        testMerchant.setId(1L);
        testMerchant.setName("Test Merchant One");
        testMerchant.setContactName("John D.");
        testMerchant.setContactEmail("john.d@example.com");
        testMerchant.setStatus(0); // Pending
        testMerchant.setCreateTime(new Date());

        testQualification = new MerMerchantQualification();
        testQualification.setId(10L);
        testQualification.setMerchantId(1L);
        testQualification.setBusinessLicenseNo("B123");
        testQualification.setReviewStatus(0); // Pending
        testQualification.setSubmissionDate(new Date());
        
        testLevelBasic = new MerMerchantLevel();
        testLevelBasic.setId(1L);
        testLevelBasic.setLevelName("基础版");
        testLevelBasic.setDescription("Basic Level");

        testLevelStandard = new MerMerchantLevel();
        testLevelStandard.setId(2L);
        testLevelStandard.setLevelName("标准版");
        testLevelStandard.setDescription("Standard Level");
    }

    @Test
    void testListApplications() {
        // Arrange
        List<MerMerchant> merchants = new ArrayList<>();
        merchants.add(testMerchant);
        when(merchantMapper.selectByExample(any(MerMerchantExample.class))).thenReturn(merchants);
        when(merchantQualificationMapper.selectByExample(any(MerMerchantQualificationExample.class)))
            .thenReturn(Collections.singletonList(testQualification)); // Ensure qualification is found

        // Act
        List<MerchantApplicationDto> result = merchantApplicationService.listApplications("Test", 0, 10, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMerchant.getName(), result.get(0).getName());
        verify(merchantMapper).selectByExample(any(MerMerchantExample.class));
    }

    @Test
    void testGetApplicationDetail_Success() {
        // Arrange
        MerMerchantPackage activePackage = new MerMerchantPackage();
        activePackage.setLevelId(testLevelBasic.getId());
        activePackage.setCommissionRate(new BigDecimal("0.05"));
        activePackage.setIsActive(true);


        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        when(merchantQualificationMapper.selectByExample(any(MerMerchantQualificationExample.class)))
            .thenReturn(Collections.singletonList(testQualification));
        when(merchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
            .thenReturn(Collections.singletonList(activePackage));
        when(merchantLevelMapper.selectByPrimaryKey(testLevelBasic.getId())).thenReturn(testLevelBasic);


        // Act
        MerchantApplicationDetailDto result = merchantApplicationService.getApplicationDetail(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMerchant.getName(), result.getName());
        assertEquals(testQualification.getBusinessLicenseNo(), result.getBusinessLicenseNo());
        assertEquals(testLevelBasic.getLevelName(), result.getCurrentPackageLevelName());
        verify(merchantMapper).selectByPrimaryKey(1L);
        verify(merchantQualificationMapper).selectByExample(any(MerMerchantQualificationExample.class));
    }
    
    @Test
    void testGetApplicationDetail_NotFound() {
        when(merchantMapper.selectByPrimaryKey(2L)).thenReturn(null);
        MerchantApplicationDetailDto result = merchantApplicationService.getApplicationDetail(2L);
        assertNull(result);
    }


    @Test
    void testApproveApplication_Success() {
        // Arrange
        ApplicationApprovalParam approvalParam = new ApplicationApprovalParam();
        approvalParam.setSelectedPackageLevelId(testLevelBasic.getId());
        approvalParam.setReviewNotes("Looks good!");

        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant); // Status is 0 (Pending)
        when(merchantQualificationMapper.selectByExample(any(MerMerchantQualificationExample.class)))
            .thenReturn(Collections.singletonList(testQualification));
        when(merchantLevelMapper.selectByPrimaryKey(testLevelBasic.getId())).thenReturn(testLevelBasic);
        when(merchantMapper.updateByPrimaryKeySelective(any(MerMerchant.class))).thenReturn(1);
        when(merchantQualificationMapper.updateByPrimaryKeySelective(any(MerMerchantQualification.class))).thenReturn(1);
        when(merchantPackageMapper.insertSelective(any(MerMerchantPackage.class))).thenReturn(1);

        // Act
        MerMerchant result = merchantApplicationService.approveApplication(1L, approvalParam);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getStatus()); // Approved (Pending Activation)

        ArgumentCaptor<MerMerchant> merchantCaptor = ArgumentCaptor.forClass(MerMerchant.class);
        verify(merchantMapper).updateByPrimaryKeySelective(merchantCaptor.capture());
        assertEquals(1, merchantCaptor.getValue().getStatus());

        ArgumentCaptor<MerMerchantQualification> qualCaptor = ArgumentCaptor.forClass(MerMerchantQualification.class);
        verify(merchantQualificationMapper).updateByPrimaryKeySelective(qualCaptor.capture());
        assertEquals(1, qualCaptor.getValue().getReviewStatus());
        assertEquals("Looks good!", qualCaptor.getValue().getReviewNotes());

        ArgumentCaptor<MerMerchantPackage> packageCaptor = ArgumentCaptor.forClass(MerMerchantPackage.class);
        verify(merchantPackageMapper).insertSelective(packageCaptor.capture());
        assertEquals(testLevelBasic.getId(), packageCaptor.getValue().getLevelId());
        assertEquals(0.05, packageCaptor.getValue().getCommissionRate().doubleValue(), 0.001); // Basic level 5%
        assertTrue(packageCaptor.getValue().getIsActive());
    }

    @Test
    void testApproveApplication_AlreadyApproved() {
        testMerchant.setStatus(1); // Already Approved
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);

        ApplicationApprovalParam approvalParam = new ApplicationApprovalParam();
        approvalParam.setSelectedPackageLevelId(1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            merchantApplicationService.approveApplication(1L, approvalParam);
        });
        assertEquals("Application is not in a pending state, cannot approve.", exception.getMessage());
    }
    
    @Test
    void testApproveApplication_MerchantNotFound() {
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(null);
        ApplicationApprovalParam approvalParam = new ApplicationApprovalParam();
        approvalParam.setSelectedPackageLevelId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            merchantApplicationService.approveApplication(1L, approvalParam);
        });
        assertEquals("Merchant application not found.", exception.getMessage());
    }

    @Test
    void testRejectApplication_Success() {
        ApplicationRejectionParam rejectionParam = new ApplicationRejectionParam();
        rejectionParam.setReviewNotes("Information mismatch.");

        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant); // Status is 0 (Pending)
        when(merchantQualificationMapper.selectByExample(any(MerMerchantQualificationExample.class)))
            .thenReturn(Collections.singletonList(testQualification));
        when(merchantMapper.updateByPrimaryKeySelective(any(MerMerchant.class))).thenReturn(1);
        when(merchantQualificationMapper.updateByPrimaryKeySelective(any(MerMerchantQualification.class))).thenReturn(1);

        MerMerchant result = merchantApplicationService.rejectApplication(1L, rejectionParam);

        assertNotNull(result);
        assertEquals(2, result.getStatus()); // Rejected

        ArgumentCaptor<MerMerchant> merchantCaptor = ArgumentCaptor.forClass(MerMerchant.class);
        verify(merchantMapper).updateByPrimaryKeySelective(merchantCaptor.capture());
        assertEquals(2, merchantCaptor.getValue().getStatus());

        ArgumentCaptor<MerMerchantQualification> qualCaptor = ArgumentCaptor.forClass(MerMerchantQualification.class);
        verify(merchantQualificationMapper).updateByPrimaryKeySelective(qualCaptor.capture());
        assertEquals(2, qualCaptor.getValue().getReviewStatus());
        assertEquals("Information mismatch.", qualCaptor.getValue().getReviewNotes());
    }
    
    @Test
    void testUpdateMerchantStatus_Activate() {
        testMerchant.setStatus(1); // Approved (Pending Activation)
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        when(merchantMapper.updateByPrimaryKeySelective(any(MerMerchant.class))).thenReturn(1);

        MerMerchant result = merchantApplicationService.updateMerchantStatus(1L, 3); // Activate

        assertEquals(3, result.getStatus());
        verify(merchantMapper).updateByPrimaryKeySelective(testMerchant);
    }

    @Test
    void testUpdateMerchantStatus_Deactivate() {
        testMerchant.setStatus(3); // Active
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        when(merchantMapper.updateByPrimaryKeySelective(any(MerMerchant.class))).thenReturn(1);

        MerMerchant result = merchantApplicationService.updateMerchantStatus(1L, 4); // Deactivate

        assertEquals(4, result.getStatus());
        verify(merchantMapper).updateByPrimaryKeySelective(testMerchant);
    }

    @Test
    void testUpdateMerchantStatus_InvalidInitialState() {
        testMerchant.setStatus(0); // Pending
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            merchantApplicationService.updateMerchantStatus(1L, 3);
        });
        assertEquals("Merchant is not in a state that allows status change to Active/Inactive.", exception.getMessage());
    }
    
    @Test
    void testUpdateMerchantStatus_InvalidTargetStatus() {
        testMerchant.setStatus(1); // Approved
        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            merchantApplicationService.updateMerchantStatus(1L, 0); // Trying to set to Pending
        });
        assertEquals("Invalid status value for activation/deactivation.", exception.getMessage());
    }

    @Test
    void testCreateMerchantLevel_Success() {
        MerMerchantLevel newLevel = new MerMerchantLevel();
        newLevel.setLevelName("Gold Tier");
        newLevel.setDescription("Gold level benefits");

        when(merchantLevelMapper.insertSelective(any(MerMerchantLevel.class))).thenAnswer(invocation -> {
            MerMerchantLevel level = invocation.getArgument(0);
            level.setId(3L); // Simulate ID generation
            return 1;
        });

        MerMerchantLevel result = merchantApplicationService.createMerchantLevel(newLevel);

        assertNotNull(result);
        assertEquals("Gold Tier", result.getLevelName());
        assertNotNull(result.getId());
        verify(merchantLevelMapper).insertSelective(any(MerMerchantLevel.class));
    }
    
    @Test
    void testUpdateMerchantLevel_Success() {
        MerMerchantLevel existingLevel = new MerMerchantLevel();
        existingLevel.setId(1L);
        existingLevel.setLevelName("Old Name");

        MerMerchantLevel updates = new MerMerchantLevel();
        updates.setLevelName("New Name");
        updates.setDescription("New Description");

        when(merchantLevelMapper.updateByPrimaryKeySelective(any(MerMerchantLevel.class))).thenReturn(1);
        when(merchantLevelMapper.selectByPrimaryKey(1L)).thenReturn(updates); // Return the updated object

        MerMerchantLevel result = merchantApplicationService.updateMerchantLevel(1L, updates);

        assertNotNull(result);
        assertEquals("New Name", result.getLevelName());
        verify(merchantLevelMapper).updateByPrimaryKeySelective(any(MerMerchantLevel.class));
    }


    @Test
    void testAssignOrUpdateMerchantPackage_NewPackage() {
        MerchantPackageAssignParam assignParam = new MerchantPackageAssignParam();
        assignParam.setLevelId(testLevelStandard.getId());
        assignParam.setCommissionRate(new BigDecimal("0.025"));
        assignParam.setStartDate(new Date());
        assignParam.setEndDate(new Date(System.currentTimeMillis() + 100000000));
        assignParam.setIsActive(true);

        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        when(merchantLevelMapper.selectByPrimaryKey(testLevelStandard.getId())).thenReturn(testLevelStandard);
        when(merchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class))).thenReturn(new ArrayList<>()); // No existing active package
        when(merchantPackageMapper.insertSelective(any(MerMerchantPackage.class))).thenAnswer(invocation -> {
            MerMerchantPackage pkg = invocation.getArgument(0);
            pkg.setId(100L); // Simulate ID generation
            return 1;
        });

        MerMerchantPackage result = merchantApplicationService.assignOrUpdateMerchantPackage(1L, assignParam);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(testLevelStandard.getId(), result.getLevelId());
        assertEquals(0.025, result.getCommissionRate().doubleValue(), 0.0001);
        assertTrue(result.getIsActive());
        verify(merchantPackageMapper).insertSelective(any(MerMerchantPackage.class));
        verify(merchantPackageMapper, never()).updateByPrimaryKeySelective(any(MerMerchantPackage.class)); // Ensure no update for old package
    }

    @Test
    void testAssignOrUpdateMerchantPackage_DeactivateOldPackage() {
        MerchantPackageAssignParam assignParam = new MerchantPackageAssignParam();
        assignParam.setLevelId(testLevelStandard.getId());
        assignParam.setCommissionRate(new BigDecimal("0.025"));
        assignParam.setStartDate(new Date());
        assignParam.setEndDate(new Date(System.currentTimeMillis() + 100000000));
        assignParam.setIsActive(true);

        MerMerchantPackage oldActivePackage = new MerMerchantPackage();
        oldActivePackage.setId(99L);
        oldActivePackage.setMerchantId(1L);
        oldActivePackage.setLevelId(testLevelBasic.getId());
        oldActivePackage.setIsActive(true);

        when(merchantMapper.selectByPrimaryKey(1L)).thenReturn(testMerchant);
        when(merchantLevelMapper.selectByPrimaryKey(testLevelStandard.getId())).thenReturn(testLevelStandard);
        when(merchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class)))
            .thenReturn(Collections.singletonList(oldActivePackage)); // Existing active package
        when(merchantPackageMapper.updateByPrimaryKeySelective(any(MerMerchantPackage.class))).thenReturn(1);
        when(merchantPackageMapper.insertSelective(any(MerMerchantPackage.class))).thenAnswer(invocation -> {
            MerMerchantPackage pkg = invocation.getArgument(0);
            pkg.setId(101L);
            return 1;
        });
        
        MerMerchantPackage result = merchantApplicationService.assignOrUpdateMerchantPackage(1L, assignParam);

        assertNotNull(result);
        assertEquals(101L, result.getId());

        ArgumentCaptor<MerMerchantPackage> oldPackageCaptor = ArgumentCaptor.forClass(MerMerchantPackage.class);
        verify(merchantPackageMapper).updateByPrimaryKeySelective(oldPackageCaptor.capture());
        assertFalse(oldPackageCaptor.getValue().getIsActive()); // Verify old package was deactivated

        ArgumentCaptor<MerMerchantPackage> newPackageCaptor = ArgumentCaptor.forClass(MerMerchantPackage.class);
        verify(merchantPackageMapper).insertSelective(newPackageCaptor.capture());
        assertTrue(newPackageCaptor.getValue().getIsActive());
    }

    @Test
    void testGetMerchantPackageHistory() {
        List<MerMerchantPackage> packages = new ArrayList<>();
        packages.add(new MerMerchantPackage());
        packages.add(new MerMerchantPackage());
        when(merchantPackageMapper.selectByExample(any(MerMerchantPackageExample.class))).thenReturn(packages);

        List<MerMerchantPackage> result = merchantApplicationService.getMerchantPackageHistory(1L);

        assertEquals(2, result.size());
        verify(merchantPackageMapper).selectByExample(any(MerMerchantPackageExample.class));
    }
}
