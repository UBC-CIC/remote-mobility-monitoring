package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPatientRequestBody {
    @SerializedName("caregiver_id")
    private String caregiverId;
    @SerializedName("patient_id")
    private String patientId;
    @SerializedName("auth_code")
    private String authCode;
    @SerializedName("device_id")
    private String deviceId;
}
