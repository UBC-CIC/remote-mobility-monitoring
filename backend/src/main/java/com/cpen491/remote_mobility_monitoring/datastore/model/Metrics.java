package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    public enum MeasureName {
        STEP_LENGTH("step_length"),
        DOUBLE_SUPPORT_TIME("double_support_time"),
        WALKING_SPEED("walking_speed"),
        WALKING_ASYMMETRY("walking_asymmetry"),
        DISTANCE_WALKED("distance_walked");

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

    private String patientId;
    private String deviceId;
    private MeasureName measureName;
    private String measureValue;
    private String timestamp;
}
