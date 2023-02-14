package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class GetAllPatientsHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Get all Patients request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            GetAllPatientsRequestBody requestBody = GetAllPatientsRequestBody.builder()
                    .caregiverId(caregiverId)
                    .build();
            GetAllPatientsResponseBody responseBody = caregiverService.getAllPatients(requestBody);
            log.info("Responding to Get all Patients request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
