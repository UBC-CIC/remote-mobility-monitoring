package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
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
        DISTANCE_WALKED(MetricsTable.DISTANCE_WALKED_NAME),
        STEP_COUNT(MetricsTable.STEP_COUNT_NAME);

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
    private MeasureName measureName;
    private String measureValue;
    private String timestamp;
}
