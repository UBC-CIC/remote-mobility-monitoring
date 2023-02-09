package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMetricsResponseBody {
    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class QueryMetricsSerialization {
        @SerializedName(Const.PATIENT_ID_NAME)
        private String patientId;
        @SerializedName(Const.METRIC_NAME_NAME)
        private String metricName;
        @SerializedName(Const.METRIC_VALUE_NAME)
        private String metricValue;
        @SerializedName(Const.TIMESTAMP_NAME)
        private String timestamp;

        public static List<QueryMetricsSerialization> convertFromMetrics(List<Metrics> metrics) {
            return metrics.stream().map(metric -> QueryMetricsSerialization.builder()
                    .patientId(metric.getPatientId())
                    .metricName(metric.getMeasureName().type)
                    .metricValue(metric.getMeasureValue())
                    .timestamp(metric.getTimestamp())
                    .build()).collect(Collectors.toList());
        }
    }

    @SerializedName(Const.METRICS_NAME)
    private List<QueryMetricsSerialization> metrics;
}
