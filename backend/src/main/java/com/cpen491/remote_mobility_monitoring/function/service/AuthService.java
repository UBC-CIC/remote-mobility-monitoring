package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InsufficientPermissionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * Verifies that Caregiver has Patient
     *
     * @param caregiverId The id of the Caregiver
     * @param patientId The id of the Patient
     * @throws InsufficientPermissionException If Caregiver does not have Patient
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void caregiverHasPatient(String caregiverId, String patientId) {
        if (!caregiverDao.hasPatient(patientId, caregiverId)) {
            log.error("Caregiver [{}] does not have Patient [{}]", caregiverId, patientId);
            throw new InsufficientPermissionException();
        }
    }
}
