package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.dependency.utility.JwtUtils;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class AddPatientPrimaryHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Add Patient Primary request with path parameters: {} and body: {}",
                requestEvent.getPathParameters(), requestEvent.getBody());
        return processApiGatewayRequest((request) -> {
            String rawId = JwtUtils.getIdFromHeader(request.getHeaders(), gson);
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            authService.selfCheckThrow(rawId, caregiverId);
            AddPatientPrimaryRequestBody requestBody = gson.fromJson(request.getBody(), AddPatientPrimaryRequestBody.class);
            requestBody.setCaregiverId(caregiverId);
            AddPatientPrimaryResponseBody responseBody = caregiverService.addPatientPrimary(requestBody);
            log.info("Responding to Add Patient Primary request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
