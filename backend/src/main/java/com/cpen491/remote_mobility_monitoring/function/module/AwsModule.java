package com.cpen491.remote_mobility_monitoring.function.module;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import javax.inject.Singleton;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.convertToAttributeValue;

@Module
public class AwsModule {
    private static final Region REGION = Region.US_WEST_2;

    // Prevent lazy loading DynamoDbClient by using static initializer
    // This creates DynamoDbClient and significantly reduces cold start time
    static {
        try {
            DynamoDbClient ddbClient = dynamoDbClient(httpClient());
            Map<String, AttributeValue> keyMap = new HashMap<>();
            keyMap.put(BaseTable.PID_NAME, convertToAttributeValue("org-1"));
            keyMap.put(BaseTable.SID_NAME, convertToAttributeValue("org-1"));
            ddbClient.getItem(GetItemRequest.builder().key(keyMap).build());
        } catch (Exception e) {
            // Expects exception
        }
    }

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
                .region(Region.US_WEST_2)
                .endpointOverride(URI.create("https://dynamodb.us-west-2.amazonaws.com"))
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }
}
