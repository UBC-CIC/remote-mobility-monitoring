package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.google.gson.Gson;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

public class CreateCaregiverHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CaregiverService caregiverService;
    private final Gson gson;

    public CreateCaregiverHandler() {
        Config config = Config.instance();
        this.caregiverService = config.caregiverService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return processApiGatewayRequest((request) -> {
            CreateCaregiverRequestBody requestBody = gson.fromJson(request.getBody(), CreateCaregiverRequestBody.class);
            CreateCaregiverResponseBody responseBody = caregiverService.createCaregiver(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
