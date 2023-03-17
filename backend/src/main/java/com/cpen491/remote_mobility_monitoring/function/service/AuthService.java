package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InsufficientPermissionException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;

// TODO: Unit test
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    @NonNull
    private OrganizationDao organizationDao;
    @NonNull
    private AdminDao adminDao;
    @NonNull
    private CaregiverDao caregiverDao;
    @NonNull
    private PatientDao patientDao;

    /**
     * Verifies that the user sending the request is same as the resource entity.
     * For example, for delete Admin API, only the same admin should be able to delete him/herself.
     *
     * @param rawId The id without prefix
     * @param expectedId The expected id
     * @return {@link Boolean}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if expectedId is empty or invalid
     */
    public boolean selfCheck(String rawId, String expectedId) {
        Validator.validateUserId(expectedId);

        return rawId.equals(expectedId.substring(4));
    }

    /**
     * Verifies that the user sending the request is same as the resource entity. Throws exception if self check fails.
     * For example, for delete Admin API, only the same admin should be able to delete him/herself.
     *
     * @param rawId The id without prefix
     * @param expectedId The expected id
     * @throws InsufficientPermissionException If raw id is not the same as the expectedId
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if expectedId is empty or invalid
     */
    public void selfCheckThrow(String rawId, String expectedId) {
        if (!selfCheck(rawId, expectedId)) {
            throw new InsufficientPermissionException();
        }
    }

    /**
     * Verifies that user is an Admin.
     *
     * @param rawId The user rawId without prefix
     * @throws InsufficientPermissionException If rawId does not correspond to an Admin record
     */
    public void isAdmin(String rawId) {
        try {
            String id = AdminTable.ID_PREFIX + rawId;
            adminDao.findById(id);
        } catch (Exception e) {
            log.error("[{}] does not correspond to an admin record", rawId);
            throw new InsufficientPermissionException();
        }
    }

    /**
     * Verifies that user is a Caregiver.
     *
     * @param rawId The user rawId without prefix
     * @throws InsufficientPermissionException If rawId does not correspond to a Caregiver record
     */
    public void isCaregiver(String rawId) {
        try {
            String id = CaregiverTable.ID_PREFIX + rawId;
            caregiverDao.findById(id);
        } catch (Exception e) {
            log.error("[{}] does not correspond to a caregiver record", rawId);
            throw new InsufficientPermissionException();
        }
    }

    /**
     * Verifies that Caregiver is the primary Caregiver of Patient.
     *
     * @param caregiverId The id of the Caregiver
     * @param patientId The id of the Patient
     * @throws InsufficientPermissionException If Caregiver does not have Patient
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void caregiverIsPrimaryCaregiverOfPatient(String caregiverId, String patientId) {
        caregiverId = CaregiverTable.ID_PREFIX + caregiverId;

        if (!caregiverDao.isPrimaryCaregiverOfPatient(patientId, caregiverId)) {
            log.error("Caregiver [{}] is not the primary caregiver of Patient [{}]", caregiverId, patientId);
            throw new InsufficientPermissionException();
        }
    }

    /**
     * Verifies that Caregiver has Patient.
     *
     * @param caregiverId The id of the Caregiver
     * @param patientId The id of the Patient
     * @throws InsufficientPermissionException If Caregiver does not have Patient
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void caregiverHasPatient(String caregiverId, String patientId) {
        caregiverId = CaregiverTable.ID_PREFIX + caregiverId;

        if (!caregiverDao.hasPatient(patientId, caregiverId)) {
            log.error("Caregiver [{}] does not have Patient [{}]", caregiverId, patientId);
            throw new InsufficientPermissionException();
        }
    }
}
