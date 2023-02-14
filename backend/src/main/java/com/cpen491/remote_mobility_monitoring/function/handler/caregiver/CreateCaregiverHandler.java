package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class CreateCaregiverHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Create Caregiver request with body: {}", requestEvent.getBody());
        return processApiGatewayRequest((request) -> {
            CreateCaregiverRequestBody requestBody = gson.fromJson(request.getBody(), CreateCaregiverRequestBody.class);
            CreateCaregiverResponseBody responseBody = caregiverService.createCaregiver(requestBody);
            log.info("Responding to Create Caregiver request with caregiverId {}", responseBody.getCaregiverId());
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
