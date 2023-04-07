package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPatientsResponseBody {
    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class PatientSerialization {
        @SerializedName(Const.PATIENT_ID_NAME)
        private String patientId;
        @SerializedName(Const.EMAIL_NAME)
        private String email;
        @SerializedName(Const.FIRST_NAME_NAME)
        private String firstName;
        @SerializedName(Const.LAST_NAME_NAME)
        private String lastName;
        @SerializedName(Const.PHONE_NUMBER_NAME)
        private String phoneNumber;
        @SerializedName(Const.IS_PRIMARY_NAME)
        private Boolean isPrimary;
        @SerializedName(Const.VERIFIED_NAME)
        private Boolean verified;

        public static PatientSerialization fromPatient(Patient patient) {
            return PatientSerialization.builder()
                    .patientId(patient.getPid())
                    .email(patient.getEmail())
                    .firstName(patient.getFirstName())
                    .lastName(patient.getLastName())
                    .phoneNumber(patient.getPhoneNumber())
                    .isPrimary(patient.getIsPrimary())
                    .verified(patient.getVerified())
                    .build();
        }
    }

    @SerializedName(Const.PATIENTS_NAME)
    private List<PatientSerialization> patients;
}
