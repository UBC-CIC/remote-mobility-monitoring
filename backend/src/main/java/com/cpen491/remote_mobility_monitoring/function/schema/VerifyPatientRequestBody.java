package com.cpen491.remote_mobility_monitoring.function.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPatientRequestBody {
    private String caregiverId;
    private String patientId;
    private String authCode;
    private String deviceId;
}
