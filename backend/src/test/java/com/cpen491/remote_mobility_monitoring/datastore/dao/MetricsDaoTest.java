package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidMetricsException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MeasureName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.RejectedRecordsException;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DEVICE_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.IDS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.MEASURE_NAME_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.MEASURE_VALUE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.MEASURE_VALUE_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.METRICS_LIST_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.METRICS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TIMESTAMP_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TIMESTAMP_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MetricsDaoTest {
    private static final String PATIENT_ID = "pat-1";
    private static final String DEVICE_ID = "device-id-1";
    private static final MeasureName MEASURE_NAME = MeasureName.STEP_LENGTH;
    private static final String MEASURE_VALUE = "3.0";
    private static final String INVALID_MEASURE_VALUE = "0.9%";
    private static final String TIMESTAMP = "2023-02-01T00:12:30.10101";
    private static final String INVALID_TIMESTAMP = "2023-02-01 12:00:00";

    MetricsDao cut;
    @Mock
    TimestreamWriteClient writeClient;
    @Mock
    TimestreamQueryClient queryClient;

    @BeforeEach
    public void setup() {
        cut = new MetricsDao("REMOTE_MOBILITY_MONITORING_DATABASE-dev", "METRICS-dev", writeClient, queryClient);
    }

    @Test
    public void testAdd_WHEN_WriteClientThrowsRejectedRecordsException_THEN_ThrowInvalidMetricsException() {
        Mockito.doThrow(RejectedRecordsException.class).when(writeClient).writeRecords(any(WriteRecordsRequest.class));

        List<Metrics> metricsList = new ArrayList<>();
        metricsList.add(buildMetrics(MeasureName.DOUBLE_SUPPORT_TIME, "0.3"));
        assertThatThrownBy(() -> cut.add(metricsList)).isInstanceOf(InvalidMetricsException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAdd")
    public void testAdd_WHEN_InvalidInput_THEN_ThrowInvalidInputException(List<Metrics> metricsList, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.add(metricsList), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAdd() {
        List<Metrics> argument2 = new ArrayList<>();
        argument2.add(null);
        List<Metrics> argument3 = new ArrayList<>();
        argument3.add(buildMetrics(null, DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument4 = new ArrayList<>();
        argument4.add(buildMetrics("", DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument5 = new ArrayList<>();
        argument5.add(buildMetrics(DEVICE_ID, DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument6 = new ArrayList<>();
        argument6.add(buildMetrics(PATIENT_ID, null, MEASURE_NAME, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument7 = new ArrayList<>();
        argument7.add(buildMetrics(PATIENT_ID, "", MEASURE_NAME, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument8 = new ArrayList<>();
        argument8.add(buildMetrics(PATIENT_ID, DEVICE_ID, null, MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument9 = new ArrayList<>();
        argument9.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, null, TIMESTAMP));
        List<Metrics> argument10 = new ArrayList<>();
        argument10.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, "", TIMESTAMP));
        List<Metrics> argument11 = new ArrayList<>();
        argument11.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, INVALID_MEASURE_VALUE, TIMESTAMP));
        List<Metrics> argument12 = new ArrayList<>();
        argument12.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, null));
        List<Metrics> argument13 = new ArrayList<>();
        argument13.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, ""));
        List<Metrics> argument14 = new ArrayList<>();
        argument14.add(buildMetrics(PATIENT_ID, DEVICE_ID, MEASURE_NAME, MEASURE_VALUE, INVALID_TIMESTAMP));
        return Stream.of(
                Arguments.of(null, METRICS_LIST_NULL_ERROR_MESSAGE),
                Arguments.of(argument2, METRICS_NULL_ERROR_MESSAGE),
                Arguments.of(argument3, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(argument4, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(argument5, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(argument6, DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(argument7, DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(argument8, MEASURE_NAME_NULL_ERROR_MESSAGE),
                Arguments.of(argument9, MEASURE_VALUE_BLANK_ERROR_MESSAGE),
                Arguments.of(argument10, MEASURE_VALUE_BLANK_ERROR_MESSAGE),
                Arguments.of(argument11, MEASURE_VALUE_INVALID_ERROR_MESSAGE),
                Arguments.of(argument12, TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(argument13, TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(argument14, TIMESTAMP_INVALID_ERROR_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForQuery")
    public void testQuery_WHEN_InvalidInput_THEN_ThrowInvalidInputException(List<String> ids, String start, String end, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.query(ids, start, end), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForQuery() {
        List<String> ids2 = new ArrayList<>();
        ids2.add(null);
        List<String> ids3 = new ArrayList<>();
        ids3.add("");
        List<String> ids4 = new ArrayList<>();
        ids4.add(DEVICE_ID);
        List<String> ids5 = new ArrayList<>();
        ids5.add(PATIENT_ID);
        return Stream.of(
                Arguments.of(null, TIMESTAMP, TIMESTAMP, IDS_NULL_ERROR_MESSAGE),
                Arguments.of(ids2, TIMESTAMP, TIMESTAMP, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(ids3, TIMESTAMP, TIMESTAMP, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(ids4, TIMESTAMP, TIMESTAMP, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(ids5, null, TIMESTAMP, TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(ids5, "", TIMESTAMP, TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(ids5, INVALID_TIMESTAMP, TIMESTAMP, TIMESTAMP_INVALID_ERROR_MESSAGE),
                Arguments.of(ids5, TIMESTAMP, null, TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(ids5, TIMESTAMP, "", TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(ids5, TIMESTAMP, INVALID_TIMESTAMP, TIMESTAMP_INVALID_ERROR_MESSAGE)
        );
    }

    private static Metrics buildMetrics(MeasureName measureName, String measureValue) {
        return buildMetrics(PATIENT_ID, DEVICE_ID, measureName, measureValue, getCurrentUtcTimeString());
    }

    private static Metrics buildMetrics(String patientId, String deviceId, MeasureName measureName, String measureValue, String timestamp) {
        return Metrics.builder()
                .patientId(patientId)
                .deviceId(deviceId)
                .measureName(measureName)
                .measureValue(measureValue)
                .timestamp(timestamp)
                .build();
    }
}
