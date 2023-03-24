package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InsufficientPermissionException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.USER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.USER_ID_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String RAW_ID1 = "1234";
    private static final String ADMIN_ID1 = "adm-1234";
    private static final String ADMIN_ID2 = "adm-5678";
    private static final String CAREGIVER_ID1 = "car-1313";
    private static final String PATIENT_ID1 = "pat-2424";
    private static final String INVALID_ADMIN_ID = "adm";
    private static final String INVALID_USER_PREFIX = "abcd";

    AuthService cut;
    @Mock
    OrganizationDao organizationDao;
    @Mock
    AdminDao adminDao;
    @Mock
    CaregiverDao caregiverDao;
    @Mock
    PatientDao patientDao;

    @BeforeEach
    void setUp() {
        cut = new AuthService(organizationDao, adminDao, caregiverDao, patientDao);
    }

    @Test
    public void testSelfCheck_WHEN_SameId_ReturnTrue() {
        boolean result = cut.selfCheck(RAW_ID1, ADMIN_ID1);

        assertTrue(result);
    }

    @Test
    public void testSelfCheck_WHEN_DifferentId_ReturnFalse() {
        boolean result = cut.selfCheck(RAW_ID1, ADMIN_ID2);

        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForSelfCheck")
    public void testSelfCheck_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String expectedId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.selfCheck(RAW_ID1, expectedId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForSelfCheck() {
        return Stream.of(
                Arguments.of(null, USER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(INVALID_ADMIN_ID, USER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(INVALID_USER_PREFIX, USER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testSelfCheckThrows_WHEN_SameId_NoThrow() {
        assertDoesNotThrow(() -> cut.selfCheckThrow(RAW_ID1, ADMIN_ID1));
    }

    @Test
    public void testSelfCheckThrows_WHEN_DifferentId_ThrowInsufficientPermissionException() {
        assertInsufficientPermissionExceptionThrown(() -> cut.selfCheckThrow(RAW_ID1, ADMIN_ID2));
    }

    @Test
    public void testIsAdmin_HappyCase() {
        assertDoesNotThrow(() -> cut.isAdmin(ADMIN_ID1));
    }

    @Test
    public void testIsAdmin_WHEN_NotAdmin_THEN_ThrowInsufficientPermissionException() {
        Mockito.doThrow(RecordDoesNotExistException.class).when(adminDao).findById(anyString());

        assertInsufficientPermissionExceptionThrown(() -> cut.isAdmin(CAREGIVER_ID1));
    }

    @Test
    public void testIsCaregiver_HappyCase() {
        assertDoesNotThrow(() -> cut.isCaregiver(CAREGIVER_ID1));
    }

    @Test
    public void testIsCaregiver_WHEN_NotCaregiver_THEN_ThrowInsufficientPermissionException() {
        Mockito.doThrow(RecordDoesNotExistException.class).when(caregiverDao).findById(anyString());

        assertInsufficientPermissionExceptionThrown(() -> cut.isCaregiver(ADMIN_ID1));
    }

    @Test
    public void testCaregiverIsPrimaryCaregiverOfPatient_HappyCase() {
        when(caregiverDao.isPrimaryCaregiverOfPatient(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> cut.caregiverIsPrimaryCaregiverOfPatient(RAW_ID1, PATIENT_ID1));
    }

    @Test
    public void testCaregiverIsPrimaryCaregiverOfPatient_WHEN_NotPrimaryCaregiver_THEN_ThrowInsufficientPermissionException() {
        when(caregiverDao.isPrimaryCaregiverOfPatient(anyString(), anyString())).thenReturn(false);

        assertInsufficientPermissionExceptionThrown(() -> cut.caregiverIsPrimaryCaregiverOfPatient(RAW_ID1, PATIENT_ID1));
    }

    @Test
    public void testCaregiverHasPatient_HappyCase() {
        when(caregiverDao.hasPatient(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> cut.caregiverHasPatient(RAW_ID1, PATIENT_ID1));
    }

    @Test
    public void testCaregiverHasPatient_WHEN_NotCaregiverOfPatient_THEN_ThrowInsufficientPermissionException() {
        when(caregiverDao.hasPatient(anyString(), anyString())).thenReturn(false);

        assertInsufficientPermissionExceptionThrown(() -> cut.caregiverHasPatient(RAW_ID1, PATIENT_ID1));
    }

    private static void assertInsufficientPermissionExceptionThrown(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
        assertThatThrownBy(shouldRaiseThrowable).isInstanceOf(InsufficientPermissionException.class);
    }
}
