package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MeasureName;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.MetricsTable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMetricsRequestBody {
    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class MetricsSerialization {
        @SerializedName(MetricsTable.STEP_LENGTH_NAME)
        private String stepLength;
        @SerializedName(MetricsTable.DOUBLE_SUPPORT_TIME_NAME)
        private String doubleSupportTime;
        @SerializedName(MetricsTable.WALKING_SPEED_NAME)
        private String walkingSpeed;
        @SerializedName(MetricsTable.WALKING_ASYMMETRY_NAME)
        private String walkingAsymmetry;
        @SerializedName(MetricsTable.DISTANCE_WALKED_NAME)
        private String distanceWalked;
        @SerializedName(MetricsTable.TIMESTAMP_NAME)
        private String timestamp;

        public static List<Metrics> convertToMetrics(String patientId, String deviceId, MetricsSerialization serialization) {
            List<Metrics> metricsList = new ArrayList<>();

            metricsList.add(Metrics.builder().measureName(MeasureName.STEP_LENGTH).measureValue(serialization.getStepLength()).build());
            metricsList.add(Metrics.builder().measureName(MeasureName.DOUBLE_SUPPORT_TIME).measureValue(serialization.getDoubleSupportTime()).build());
            metricsList.add(Metrics.builder().measureName(MeasureName.WALKING_SPEED).measureValue(serialization.getWalkingSpeed()).build());
            metricsList.add(Metrics.builder().measureName(MeasureName.WALKING_ASYMMETRY).measureValue(serialization.getWalkingAsymmetry()).build());
            metricsList.add(Metrics.builder().measureName(MeasureName.DISTANCE_WALKED).measureValue(serialization.getDistanceWalked()).build());

            for (Metrics metrics : metricsList) {
                metrics.setPatientId(patientId);
                metrics.setDeviceId(deviceId);
                metrics.setTimestamp(serialization.getTimestamp());
            }
            return metricsList;
        }
    }

    @SerializedName(Const.DEVICE_ID_NAME)
    private String deviceId;
    @SerializedName(Const.METRICS_NAME)
    private List<MetricsSerialization> metrics;
}
