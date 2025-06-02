package com.macro.mall.portal.service;

import com.macro.mall.mapper.MerMerchantMapper;
import com.macro.mall.mapper.MerMerchantQualificationMapper;
import com.macro.mall.model.MerMerchant;
import com.macro.mall.model.MerMerchantQualification;
import com.macro.mall.portal.domain.MerchantQualificationParam;
import com.macro.mall.portal.domain.MerchantRegistrationParam;
import com.macro.mall.portal.service.impl.PortalMerchantApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortalMerchantApplicationServiceImplTest {

    @Mock
    private MerMerchantMapper merchantMapper;

    @Mock
    private MerMerchantQualificationMapper merchantQualificationMapper;

    @InjectMocks
    private PortalMerchantApplicationServiceImpl merchantApplicationService;

    private MerchantRegistrationParam registrationParam;
    private MerchantQualificationParam qualificationParam;

    @BeforeEach
    void setUp() {
        qualificationParam = new MerchantQualificationParam();
        qualificationParam.setBusinessLicenseNo("12345XYZ");
        qualificationParam.setBusinessLicenseImageUrl("http://example.com/license.jpg");
        qualificationParam.setLegalRepresentativeIdFrontUrl("http://example.com/id_front.jpg");
        qualificationParam.setLegalRepresentativeIdBackUrl("http://example.com/id_back.jpg");

        registrationParam = new MerchantRegistrationParam();
        registrationParam.setName("Test Merchant");
        registrationParam.setContactName("John Doe");
        registrationParam.setContactPhone("1234567890");
        registrationParam.setContactEmail("john.doe@example.com");
        registrationParam.setAddress("123 Test St, Test City");
        registrationParam.setQualifications(qualificationParam);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        MerMerchant merchantToSave = new MerMerchant();
        BeanUtils.copyProperties(registrationParam, merchantToSave);
        merchantToSave.setStatus(0); // Pending
        // Simulate ID generation by mapper
        doAnswer(invocation -> {
            MerMerchant m = invocation.getArgument(0);
            m.setId(1L); // Set a dummy ID
            return 1; // Return value for insertSelective typically int (rows affected)
        }).when(merchantMapper).insertSelective(any(MerMerchant.class));

        when(merchantQualificationMapper.insertSelective(any(MerMerchantQualification.class))).thenReturn(1);

        // Act
        MerMerchant result = merchantApplicationService.register(registrationParam);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId()); // Check if ID was set
        assertEquals(registrationParam.getName(), result.getName());
        assertEquals(0, result.getStatus()); // Check status

        ArgumentCaptor<MerMerchant> merchantCaptor = ArgumentCaptor.forClass(MerMerchant.class);
        verify(merchantMapper).insertSelective(merchantCaptor.capture());
        MerMerchant capturedMerchant = merchantCaptor.getValue();
        assertEquals(0, capturedMerchant.getStatus());
        assertNotNull(capturedMerchant.getCreateTime());
        assertNotNull(capturedMerchant.getUpdateTime());

        ArgumentCaptor<MerMerchantQualification> qualificationCaptor = ArgumentCaptor.forClass(MerMerchantQualification.class);
        verify(merchantQualificationMapper).insertSelective(qualificationCaptor.capture());
        MerMerchantQualification capturedQualification = qualificationCaptor.getValue();
        assertEquals(result.getId(), capturedQualification.getMerchantId());
        assertEquals(0, capturedQualification.getReviewStatus());
        assertEquals(qualificationParam.getBusinessLicenseNo(), capturedQualification.getBusinessLicenseNo());
        assertNotNull(capturedQualification.getSubmissionDate());
    }
    
    @Test
    void testRegister_Success_SimulateRealIdInService() {
        // Arrange
        // Simulate that merchantMapper.insertSelective sets the ID on the passed object
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((MerMerchant)args[0]).setId(123L); // Simulate ID generation
            return 1; // rows affected
        }).when(merchantMapper).insertSelective(any(MerMerchant.class));

        when(merchantQualificationMapper.insertSelective(any(MerMerchantQualification.class))).thenReturn(1);

        // Act
        MerMerchant result = merchantApplicationService.register(registrationParam);

        // Assert
        assertNotNull(result);
        assertEquals(123L, result.getId()); // Check if ID was set by the mock
        assertEquals(registrationParam.getName(), result.getName());
        assertEquals(0, result.getStatus());

        ArgumentCaptor<MerMerchantQualification> qualificationCaptor = ArgumentCaptor.forClass(MerMerchantQualification.class);
        verify(merchantQualificationMapper).insertSelective(qualificationCaptor.capture());
        assertEquals(123L, qualificationCaptor.getValue().getMerchantId());
    }


    @Test
    void testRegister_NullMerchantName() {
        registrationParam.setName(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            merchantApplicationService.register(registrationParam);
        });
        assertEquals("Merchant name cannot be empty.", exception.getMessage());
        verify(merchantMapper, never()).insertSelective(any());
        verify(merchantQualificationMapper, never()).insertSelective(any());
    }

    @Test
    void testRegister_EmptyMerchantName() {
        registrationParam.setName("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            merchantApplicationService.register(registrationParam);
        });
        assertEquals("Merchant name cannot be empty.", exception.getMessage());
         verify(merchantMapper, never()).insertSelective(any());
        verify(merchantQualificationMapper, never()).insertSelective(any());
    }
    
    @Test
    void testRegister_NullQualifications() {
        registrationParam.setQualifications(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Need to mock merchantMapper for this to pass the first part
            doAnswer(invocation -> {
                 MerMerchant m = invocation.getArgument(0);
                 m.setId(1L);
                 return 1;
            }).when(merchantMapper).insertSelective(any(MerMerchant.class));
            merchantApplicationService.register(registrationParam);
        });
        assertEquals("Merchant qualifications cannot be null.", exception.getMessage());
        verify(merchantMapper).insertSelective(any(MerMerchant.class)); // merchant part would be called
        verify(merchantQualificationMapper, never()).insertSelective(any()); // qualification part should not
    }

    @Test
    void testRegister_MerchantInsertFails_IdNotSet() {
         // Simulate merchantMapper.insertSelective does not set the ID (e.g., returns 0 rows affected or ID remains null)
        doAnswer(invocation -> {
            // MerMerchant m = invocation.getArgument(0);
            // m.setId(null); // Explicitly ensure ID is null or not set
            return 1; // Simulate rows affected but ID not set by some misconfiguration / DB issue
        }).when(merchantMapper).insertSelective(any(MerMerchant.class));
        // Note: The mock for merchantMapper.insertSelective in setUp or previous tests might interfere.
        // Here, we are testing the scenario where merchant.getId() returns null *after* the call.
        // The current PortalMerchantApplicationServiceImpl directly throws if merchant.getId() is null.

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            merchantApplicationService.register(registrationParam);
        });
        assertEquals("Failed to create merchant application, merchant ID not generated.", exception.getMessage());
        verify(merchantMapper).insertSelective(any(MerMerchant.class));
        verify(merchantQualificationMapper, never()).insertSelective(any());
    }
}
