package com.cpen491.remote_mobility_monitoring.function.module;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class EnvironmentModule {
    public static final String DYNAMO_DB_TABLE_NAME = "DYNAMO_DB_TABLE_NAME";
    public static final String TIMESTREAM_DATABASE_NAME = "TIMESTREAM_DATABASE_NAME";
    public static final String TIMESTREAM_TABLE_NAME = "TIMESTREAM_TABLE_NAME";
    public static final String COGNITO_USERPOOL_ID = "COGNITO_USERPOOL_ID";
    public static final String SES_SENDER = "SES_SENDER";

    @Provides
    @Named(DYNAMO_DB_TABLE_NAME)
    @Singleton
    public static String dynamoDbTableName() {
        return System.getenv(DYNAMO_DB_TABLE_NAME);
    }

    @Provides
    @Named(TIMESTREAM_DATABASE_NAME)
    @Singleton
    public static String timestreamDatabaseName() {
        return System.getenv(TIMESTREAM_DATABASE_NAME);
    }

    @Provides
    @Named(TIMESTREAM_TABLE_NAME)
    @Singleton
    public static String timestreamTableName() {
        return System.getenv(TIMESTREAM_TABLE_NAME);
    }

    @Provides
    @Named(COGNITO_USERPOOL_ID)
    @Singleton
    public static String cognitoUserpoolId() {
        return System.getenv(COGNITO_USERPOOL_ID);
    }

    @Provides
    @Named(SES_SENDER)
    @Singleton
    public static String sesSender() {
        return System.getenv(SES_SENDER);
    }
}
