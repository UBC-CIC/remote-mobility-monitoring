package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidMetricsException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MeasureName;
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
import software.amazon.awssdk.services.timestreamwrite.model.RejectedRecordsException;
import software.amazon.awssdk.services.timestreamwrite.model.WriteRecordsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.MetricsTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getTimeMillis;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.parseTime;

@Slf4j
@AllArgsConstructor
public class MetricsDao {
    private static final String QUERY_FORMAT = "SELECT * FROM \"%s\".\"%s\" WHERE patient_id in (%s) " +
            "AND time between from_iso8601_timestamp('%s') and from_iso8601_timestamp('%s') ORDER BY time";

    @NonNull
    private String databaseName;
    @NonNull
    private String tableName;
    @NonNull
    private TimestreamWriteClient writeClient;
    @NonNull
    private TimestreamQueryClient queryClient;

    /**
     * Adds a list of Metrics.
     *
     * @param metricsList The list of Metrics to add
     * @throws InvalidMetricsException If the metrics already exists or if timestamp is out of Timestream range
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of patientId, measureName,
     *                              measureValue, or timestamp are empty or invalid
     */
    public void add(List<Metrics> metricsList) {
        log.info("Adding new Metrics {}", metricsList);
        Validator.validateMetricsList(metricsList);

        List<Record> records = new ArrayList<>();

        for (Metrics metrics : metricsList) {
            Validator.validateMetrics(metrics);

            String timestamp = Long.toString(getTimeMillis(parseTime(metrics.getTimestamp())));

            List<Dimension> dimensions = new ArrayList<>();
            Dimension patientId = Dimension.builder().name(MetricsTable.PATIENT_ID_NAME).value(metrics.getPatientId()).build();
            Dimension patientSex = Dimension.builder().name(MetricsTable.PATIENT_SEX_NAME).value(metrics.getSex() == null ? null : metrics.getSex()).build();
            Dimension patientBirthday = Dimension.builder().name(MetricsTable.PATIENT_BIRTHDAY_NAME).value(metrics.getBirthday() == null ? null : metrics.getBirthday()).build();
            Dimension patientHeight = Dimension.builder().name(MetricsTable.PATIENT_HEIGHT_NAME).value(metrics.getHeight() == null ? null : metrics.getHeight().toString()).build();
            Dimension patientWeight = Dimension.builder().name(MetricsTable.PATIENT_WEIGHT_NAME).value(metrics.getWeight() == null ? null : metrics.getWeight().toString()).build();
            dimensions.addAll(List.of(patientId, patientSex, patientBirthday, patientHeight, patientWeight));

            Record record = Record.builder()
                    .dimensions(dimensions)
                    .measureName(metrics.getMeasureName().type)
                    .measureValue(metrics.getMeasureValue())
                    .measureValueType(MeasureValueType.DOUBLE)
                    .time(timestamp)
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
        } catch (RejectedRecordsException e) {
            log.error("Timestream rejected metrics {}", e.rejectedRecords(), e);
            throw new InvalidMetricsException(e);
        }
    }

    /**
     * Queries for Metrics based on patient IDs, start time, and end time.
     *
     * @param patientIds Patient Ids to query
     * @param start Start time to query
     * @param end End time to query
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of patientIds, start, or end are empty or invalid
     */
    public List<Metrics> query(List<String> patientIds, String start, String end) {
        log.info("Querying Metrics database for patients {} from {} to {}", patientIds, start, end);
        Validator.validateIds(patientIds);
        for (String patientId : patientIds) {
            Validator.validatePatientId(patientId);
        }
        Validator.validateTimestamp(start);
        Validator.validateTimestamp(end);

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
                Metrics metrics = parseRow(columnInfos, row);
                metricsList.add(metrics);
            }
        }

        return metricsList;
    }

    private static Metrics parseRow(List<ColumnInfo> columnInfos, Row row) {
        List<Datum> data = row.data();
        Metrics.MetricsBuilder metricsBuilder = Metrics.builder();

        for (int i = 0; i < data.size(); i++) {
            ColumnInfo columnInfo = columnInfos.get(i);
            Datum datum = data.get(i);
            switch (columnInfo.name()) {
                case MetricsTable.PATIENT_ID_NAME:
                    metricsBuilder.patientId(datum.scalarValue());
                    break;
                case MetricsTable.PATIENT_SEX_NAME:
                    metricsBuilder.sex(datum.scalarValue());
                    break;
                case MetricsTable.PATIENT_BIRTHDAY_NAME:
                    metricsBuilder.birthday(datum.scalarValue());
                    break;
                case MetricsTable.PATIENT_HEIGHT_NAME:
                    metricsBuilder.height(Float.parseFloat(datum.scalarValue()));
                    break;
                case MetricsTable.PATIENT_WEIGHT_NAME:
                    metricsBuilder.weight(Float.parseFloat(datum.scalarValue()));
                    break;
                case MetricsTable.MEASURE_NAME_NAME:
                    metricsBuilder.measureName(MeasureName.convertToEnum(datum.scalarValue()));
                    break;
                case MetricsTable.TIME_NAME:
                    metricsBuilder.timestamp(datum.scalarValue());
                    break;
                default:
                    if (columnInfo.name().startsWith(MetricsTable.MEASURE_VALUE_NAME)) {
                        metricsBuilder.measureValue(datum.scalarValue());
                    }
                    break;
            }
        }

        return metricsBuilder.build();
    }
}
