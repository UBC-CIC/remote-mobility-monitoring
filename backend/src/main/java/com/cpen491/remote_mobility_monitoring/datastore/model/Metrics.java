package com.cpen491.remote_mobility_monitoring.datastore.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.MetricsTable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    public enum MeasureName {
        STEP_LENGTH(MetricsTable.STEP_LENGTH_NAME),
        DOUBLE_SUPPORT_TIME(MetricsTable.DOUBLE_SUPPORT_TIME_NAME),
        WALKING_SPEED(MetricsTable.WALKING_SPEED_NAME),
        WALKING_ASYMMETRY(MetricsTable.WALKING_ASYMMETRY_NAME),
        DISTANCE_WALKED(MetricsTable.DISTANCE_WALKED_NAME);

        private static final Map<String, MeasureName> stringToEnumMap = new HashMap<>();

        static {
            for (MeasureName measureName : MeasureName.values()) {
                stringToEnumMap.put(measureName.type, measureName);
            }
        }

        public final String type;

        MeasureName(String type) {
            this.type = type;
        }

        public static MeasureName convertToEnum(String s) {
            return stringToEnumMap.get(s);
        }
    }

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
    }

    private String patientId;
    private String deviceId;
    private MeasureName measureName;
    private String measureValue;
    private String timestamp;

    public static List<Metrics> convertFromSerialization(String patientId, String deviceId, MetricsSerialization serialization) {
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
