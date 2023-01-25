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
public class CreatePatientResponseBody {
    @SerializedName("patient_id")
    private String patientId;
    @SerializedName("auth_code")
    private String authCode;
}
