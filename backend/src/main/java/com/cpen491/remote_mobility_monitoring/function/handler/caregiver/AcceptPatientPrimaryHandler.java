package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class AcceptPatientPrimaryHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Accept Patient Primary request with path parameters: {} and body: {}",
                requestEvent.getPathParameters(), requestEvent.getBody());
        return processApiGatewayRequest((request) -> {
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            AcceptPatientPrimaryRequestBody requestBody = gson.fromJson(request.getBody(), AcceptPatientPrimaryRequestBody.class);
            requestBody.setCaregiverId(caregiverId);
            requestBody.setPatientId(patientId);
            AcceptPatientPrimaryResponseBody responseBody = caregiverService.acceptPatientPrimary(requestBody);
            log.info("Responding to Accept Patient Primary request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
