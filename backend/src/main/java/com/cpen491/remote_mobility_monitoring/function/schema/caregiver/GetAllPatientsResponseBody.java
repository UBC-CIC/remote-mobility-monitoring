package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPatientsResponseBody {
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class PatientSerialization {
        @SerializedName(Const.PATIENT_ID_NAME)
        private String patientId;
        @SerializedName(Const.FIRST_NAME_NAME)
        private String firstName;
        @SerializedName(Const.LAST_NAME_NAME)
        private String lastName;

        public static PatientSerialization fromPatient(Patient patient) {
            return PatientSerialization.builder()
                    .patientId(patient.getPid())
                    .firstName(patient.getFirstName())
                    .lastName(patient.getLastName())
                    .build();
        }
    }

    @SerializedName(Const.PATIENTS_NAME)
    private List<PatientSerialization> patients;
}
