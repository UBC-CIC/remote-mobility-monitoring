package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

public class GetCaregiverHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CaregiverService caregiverService;
    private final Gson gson;

    public GetCaregiverHandler() {
        Config config = Config.instance();
        this.caregiverService = config.caregiverService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return processApiGatewayRequest((request) -> {
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            GetCaregiverRequestBody requestBody = GetCaregiverRequestBody.builder()
                    .caregiverId(caregiverId)
                    .build();
            GetCaregiverResponseBody responseBody = caregiverService.getCaregiver(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
