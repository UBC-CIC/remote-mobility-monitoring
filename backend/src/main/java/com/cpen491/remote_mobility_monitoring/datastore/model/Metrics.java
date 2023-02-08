package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    private String patientId;
    private String deviceId;
    private String measureName;
    private String measureValue;
    private String timestamp;
}
