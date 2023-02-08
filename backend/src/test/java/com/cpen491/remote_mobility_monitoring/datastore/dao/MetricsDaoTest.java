package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeMillis;

class MetricsDaoTest {
    private static final String PATIENT_ID = "pat-1";
    private static final String DEVICE_ID = "device-id-1";
    MetricsDao cut;

    @BeforeEach
    public void setup() {
        TimestreamWriteClient writeClient = TimestreamWriteClient.builder()
                .region(Region.US_WEST_2)
                .build();
        TimestreamQueryClient queryClient = TimestreamQueryClient.builder()
                .region(Region.US_WEST_2)
                .build();

        cut = new MetricsDao("REMOTE_MOBILITY_MONITORING_DATABASE-dev", "METRICS-dev", writeClient, queryClient);
    }

    @Test
    public void testAdd_HappyCase() {
        List<Metrics> metricsList = new ArrayList<>();
        metricsList.add(buildMetrics("step_length", "80.0"));
        metricsList.add(buildMetrics("double_support_time", "0.3"));
        metricsList.add(buildMetrics("walking_speed", "5"));
        metricsList.add(buildMetrics("walking_asymmetry", "0.009"));
        metricsList.add(buildMetrics("distance_walked", "6.2"));

//        cut.add(metricsList);
    }

    @Test
    public void testQuery_HappyCase() {
        List<Metrics> metricsList = cut.query(Arrays.asList("pat-1", "pat-2"), "2023-02-07", "2023-02-09");
        for (Metrics metrics : metricsList) {
            System.out.println(metrics);
        }
    }

    private static Metrics buildMetrics(String measureName, String measureValue) {
        return buildMetrics(PATIENT_ID, DEVICE_ID, measureName, measureValue, Long.toString(getCurrentUtcTimeMillis()));
    }

    private static Metrics buildMetrics(String patientId, String deviceId, String measureName, String measureValue, String timestamp) {
        return Metrics.builder()
                .patientId(patientId)
                .deviceId(deviceId)
                .measureName(measureName)
                .measureValue(measureValue)
                .timestamp(timestamp)
                .build();
    }
}
