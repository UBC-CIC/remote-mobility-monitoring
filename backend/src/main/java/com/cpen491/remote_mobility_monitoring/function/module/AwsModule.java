package com.cpen491.remote_mobility_monitoring.function.module;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

import javax.inject.Singleton;
import java.net.URI;

@Module
public class AwsModule {
    private static final Region REGION = Region.US_WEST_2;

    @Provides
    @Singleton
    public static SdkHttpClient httpClient() {
        return UrlConnectionHttpClient.builder().build();
    }

    @Provides
    @Singleton
    public static CognitoIdentityProviderClient cognitoIdentityProviderClient(SdkHttpClient httpClient) {
        return CognitoIdentityProviderClient.builder()
                .httpClient(httpClient)
                .region(REGION)
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Provides
    @Singleton
    public static DynamoDbClient dynamoDbClient(SdkHttpClient httpClient) {
        // retry policy https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/using.html
        return DynamoDbClient.builder()
                .httpClient(httpClient)
                .region(REGION)
                .endpointOverride(URI.create("https://dynamodb.us-west-2.amazonaws.com"))
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Provides
    @Singleton
    public static TimestreamWriteClient timestreamWriteClient(SdkHttpClient httpClient) {
        return TimestreamWriteClient.builder()
                .httpClient(httpClient)
                .region(REGION)
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Provides
    @Singleton
    public static TimestreamQueryClient timestreamQueryClient(SdkHttpClient httpClient) {
        return TimestreamQueryClient.builder()
                .httpClient(httpClient)
                .region(REGION)
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }
}