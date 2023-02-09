package com.cpen491.remote_mobility_monitoring.datastore;

import com.cpen491.remote_mobility_monitoring.datastore.dao.MetricsDao;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

/**
 * Factory class for DAOs for Timestream, which includes MetricsDao
 */
public class MetricsDaoFactory {
    private final String databaseName;
    private final String tableName;
    private final TimestreamWriteClient writeClient;
    private final TimestreamQueryClient queryClient;

    public MetricsDaoFactory(String databaseName, String tableName, TimestreamWriteClient writeClient, TimestreamQueryClient queryClient) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.writeClient = writeClient;
        this.queryClient = queryClient;
    }

    public MetricsDao createMetricsDao() {
        return new MetricsDao(databaseName, tableName, writeClient, queryClient);
    }
}
