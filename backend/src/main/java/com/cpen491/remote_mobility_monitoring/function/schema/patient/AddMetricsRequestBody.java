package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MetricsSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMetricsRequestBody {
    @SerializedName(Const.DEVICE_ID_NAME)
    private String deviceId;
    @SerializedName(Const.METRICS_NAME)
    private List<MetricsSerialization> metrics;
}
