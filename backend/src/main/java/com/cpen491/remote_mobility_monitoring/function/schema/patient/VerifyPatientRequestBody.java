package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.function.schema.Const;
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
    @SerializedName(Const.CAREGIVER_ID_NAME)
    private String caregiverId;
    @SerializedName(Const.PATIENT_ID_NAME)
    private String patientId;
    @SerializedName(Const.AUTH_CODE_NAME)
    private String authCode;
    @SerializedName(Const.DEVICE_ID_NAME)
    private String deviceId;
}
