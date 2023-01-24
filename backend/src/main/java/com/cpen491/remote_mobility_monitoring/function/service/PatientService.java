package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PatientService {
    @NonNull
    private PatientDao patientDao;
    @NonNull
    private CaregiverDao caregiverDao;

    public void createPatient() {
        System.out.println("Creating patient");
    }

    public void verifyPatient() {
        System.out.println("Verifying patient");
    }
}
