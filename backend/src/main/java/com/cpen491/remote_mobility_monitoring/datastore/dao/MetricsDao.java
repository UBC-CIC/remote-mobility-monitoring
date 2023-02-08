package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamquery.model.ColumnInfo;
import software.amazon.awssdk.services.timestreamquery.model.Datum;
import software.amazon.awssdk.services.timestreamquery.model.QueryRequest;
import software.amazon.awssdk.services.timestreamquery.model.QueryResponse;
import software.amazon.awssdk.services.timestreamquery.model.Row;
import software.amazon.awssdk.services.timestreamquery.paginators.QueryIterable;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValueType;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.MetricsTable;

@Slf4j
@AllArgsConstructor
public class MetricsDao {
    private static final String QUERY_FORMAT = "SELECT * FROM \"%s\".\"%s\" WHERE patient_id in (%s) " +
            "AND time between '%s' and '%s' ORDER BY time";

    @NonNull
    private String databaseName;
    @NonNull
    private String tableName;
    @NonNull
    private TimestreamWriteClient writeClient;
    @NonNull
    private TimestreamQueryClient queryClient;

    public void add(List<Metrics> metricsList) {
        log.info("Adding new Metrics {}", metricsList);
        Validator.validateMetricsList(metricsList);

        List<Record> records = new ArrayList<>();

        for (Metrics metrics : metricsList) {
            Validator.validateMetrics(metrics);

            List<Dimension> dimensions = new ArrayList<>();
            Dimension patientId = Dimension.builder().name(MetricsTable.PATIENT_ID_NAME).value(metrics.getPatientId()).build();
            Dimension sensorId = Dimension.builder().name(MetricsTable.DEVICE_ID_NAME).value(metrics.getDeviceId()).build();
            dimensions.add(patientId);
            dimensions.add(sensorId);

            Record record = Record.builder()
                    .dimensions(dimensions)
                    .measureName(metrics.getMeasureName())
                    .measureValue(metrics.getMeasureValue())
                    .measureValueType(MeasureValueType.DOUBLE)
                    .time(metrics.getTimestamp())
                    .build();
            records.add(record);
        }

        WriteRecordsRequest request = WriteRecordsRequest.builder()
                .databaseName(databaseName)
                .tableName(tableName)
                .records(records)
                .build();
        try {
            writeClient.writeRecords(request);
        } catch (Exception e) {
            log.error("Encountered error", e);
        }
    }

    public List<Metrics> query(List<String> patientIds, String start, String end) {
        // TODO: validate inputs
        patientIds = patientIds.stream().map(patientId -> "'" + patientId + "'").collect(Collectors.toList());
        String patientIdsString = String.join(", ", patientIds);
        String queryString = String.format(QUERY_FORMAT, databaseName, tableName, patientIdsString, start, end);
        List<Metrics> metricsList = new ArrayList<>();

        QueryRequest request = QueryRequest.builder().queryString(queryString).build();
        QueryIterable iterable = queryClient.queryPaginator(request);

        for (QueryResponse response : iterable) {
            List<ColumnInfo> columnInfos = response.columnInfo();
            List<Row> rows = response.rows();

            for (Row row : rows) {
                List<Datum> data = row.data();
                Metrics.MetricsBuilder metricsBuilder = Metrics.builder();

                for (int i = 0; i < data.size(); i++) {
                    ColumnInfo columnInfo = columnInfos.get(i);
                    Datum datum = data.get(i);
                    switch (columnInfo.name()) {
                        case MetricsTable.PATIENT_ID_NAME:
                            metricsBuilder.patientId(datum.scalarValue());
                            break;
                        case MetricsTable.DEVICE_ID_NAME:
                            metricsBuilder.deviceId(datum.scalarValue());
                            break;
                        case MetricsTable.MEASURE_NAME_NAME:
                            metricsBuilder.measureName(datum.scalarValue());
                            break;
                        case MetricsTable.TIME_NAME:
                            metricsBuilder.timestamp(datum.scalarValue());
                            break;
                        default:
                            metricsBuilder.measureValue(datum.scalarValue());
                            break;
                    }
                }

                metricsList.add(metricsBuilder.build());
            }

        }

        return metricsList;
    }
}
