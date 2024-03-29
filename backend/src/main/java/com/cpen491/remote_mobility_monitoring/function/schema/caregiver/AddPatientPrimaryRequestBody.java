package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

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
public class AddPatientPrimaryRequestBody {
    @SerializedName(Const.CAREGIVER_ID_NAME)
    private String caregiverId;
    @SerializedName(Const.PATIENT_EMAIL_NAME)
    private String patientEmail;
    @SerializedName(Const.SEND_EMAIL)
    private boolean sendEmail;
}
